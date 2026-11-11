CREATE OR REPLACE FUNCTION auditoria.montar_detalhes(
    tipo smallint, anterior jsonb, atual jsonb
) RETURNS jsonb LANGUAGE sql IMMUTABLE AS $$
    SELECT jsonb_build_object(
        'operacao', CASE tipo WHEN 0 THEN 'ADD' WHEN 1 THEN 'MOD' WHEN 2 THEN 'DEL' END,
        'anteriorDisponivel', tipo = 0 OR anterior IS NOT NULL,
        'antes', CASE WHEN tipo = 0 THEN NULL ELSE anterior END,
        'depois', CASE WHEN tipo = 2 THEN NULL ELSE atual END,
        'camposAlterados', CASE WHEN tipo = 1 AND anterior IS NOT NULL THEN
            (SELECT coalesce(jsonb_object_agg(chave, jsonb_build_object(
                'antes', anterior -> chave, 'depois', atual -> chave)), '{}'::jsonb)
             FROM (SELECT jsonb_object_keys(anterior || atual) AS chave) campos
             WHERE anterior -> chave IS DISTINCT FROM atual -> chave)
            ELSE '{}'::jsonb END
    )
$$
^^^
CREATE OR REPLACE FUNCTION auditoria.preencher_detalhes()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
    anterior jsonb;
    atual jsonb;
    filtro text;
BEGIN
    atual := to_jsonb(NEW) - 'rev' - 'revtype' - 'detalhes';
    IF NEW.revtype <> 0 THEN
        SELECT string_agg(format('a.%I = ($1).%I', chave, chave), ' AND ')
            INTO filtro FROM jsonb_array_elements_text(TG_ARGV[0]::jsonb) chaves(chave);
        EXECUTE format('SELECT to_jsonb(a) - ''rev'' - ''revtype'' - ''detalhes''
            FROM %I.%I a WHERE %s AND a.rev < $2 AND a.revtype <> 2
            ORDER BY a.rev DESC LIMIT 1', TG_TABLE_SCHEMA, TG_TABLE_NAME, filtro)
            INTO anterior USING NEW, NEW.rev;
    END IF;
    -- DEL preserva o estado excluido pelo store_data_at_delete do Envers.
    IF NEW.revtype = 2 THEN anterior := atual; END IF;
    NEW.detalhes := auditoria.montar_detalhes(NEW.revtype, anterior, atual);
    RETURN NEW;
END $$
^^^
DO $$
DECLARE
    tabela record;
    chaves jsonb;
    particao text;
BEGIN
    FOR tabela IN
        SELECT c.oid, c.relname
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'auditoria' AND c.relkind IN ('r', 'p')
          AND right(c.relname, 4) = '_aud'
          AND EXISTS (SELECT 1 FROM pg_attribute a
              WHERE a.attrelid = c.oid AND a.attname = 'rev' AND NOT a.attisdropped)
          AND EXISTS (SELECT 1 FROM pg_attribute a
              WHERE a.attrelid = c.oid AND a.attname = 'revtype' AND NOT a.attisdropped)
    LOOP
        -- Descobre a identidade pelo PK do Envers, inclusive IDs compostos ou com outro nome.
        SELECT jsonb_agg(a.attname ORDER BY k.ordem),
               string_agg(format('%I', a.attname), ', ' ORDER BY k.ordem)
        INTO chaves, particao
        FROM pg_constraint p
        CROSS JOIN LATERAL unnest(p.conkey) WITH ORDINALITY k(numero, ordem)
        JOIN pg_attribute a ON a.attrelid = p.conrelid AND a.attnum = k.numero
        WHERE p.conrelid = tabela.oid AND p.contype = 'p'
          AND a.attname NOT IN ('rev', 'revtype');
        IF chaves IS NULL THEN
            RAISE EXCEPTION 'Tabela de auditoria % sem chave primaria de entidade.', tabela.relname;
        END IF;
        EXECUTE format('ALTER TABLE auditoria.%I ADD COLUMN IF NOT EXISTS detalhes jsonb', tabela.relname);
        EXECUTE format('DROP TRIGGER IF EXISTS preencher_detalhes ON auditoria.%I', tabela.relname);
        EXECUTE format('CREATE TRIGGER preencher_detalhes BEFORE INSERT ON auditoria.%I
            FOR EACH ROW EXECUTE FUNCTION auditoria.preencher_detalhes(%L)', tabela.relname, chaves::text);
        -- Reconstitui detalhes de revisoes antigas sem inventar estados nao registrados.
        EXECUTE format('WITH versoes AS (
            SELECT tableoid AS tabela, ctid AS linha, revtype,
                to_jsonb(a) - ''rev'' - ''revtype'' - ''detalhes'' AS atual,
                lag(to_jsonb(a) - ''rev'' - ''revtype'' - ''detalhes'')
                    OVER (PARTITION BY %s ORDER BY rev) AS anterior
            FROM auditoria.%I a
        ) UPDATE auditoria.%I a SET detalhes = auditoria.montar_detalhes(v.revtype,
            CASE WHEN v.revtype = 2 THEN v.atual ELSE v.anterior END, v.atual)
            FROM versoes v WHERE a.tableoid = v.tabela AND a.ctid = v.linha AND a.detalhes IS NULL',
            particao, tabela.relname, tabela.relname);
    END LOOP;
END $$
^^^
