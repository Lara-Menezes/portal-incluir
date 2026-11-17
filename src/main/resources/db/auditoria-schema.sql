CREATE SCHEMA IF NOT EXISTS auditoria
^^^
DO $$
DECLARE
    tabela text;
BEGIN
    FOR tabela IN
        SELECT c.relname
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'public' AND c.relkind IN ('r', 'p')
          AND (c.relname = 'revinfo' OR (
              right(c.relname, 4) = '_aud'
              AND EXISTS (SELECT 1 FROM pg_attribute a
                  WHERE a.attrelid = c.oid AND a.attname = 'rev' AND NOT a.attisdropped)
              AND EXISTS (SELECT 1 FROM pg_attribute a
                  WHERE a.attrelid = c.oid AND a.attname = 'revtype' AND NOT a.attisdropped)))
    LOOP
        IF to_regclass('public.' || tabela) IS NOT NULL THEN
            IF to_regclass('auditoria.' || tabela) IS NOT NULL THEN
                RAISE EXCEPTION 'Existem duas tabelas de auditoria para %. Reconciliar antes de iniciar.', tabela;
            END IF;
            EXECUTE format('ALTER TABLE public.%I SET SCHEMA auditoria', tabela);
        END IF;
    END LOOP;
    IF to_regclass('public.revinfo_seq') IS NOT NULL THEN
        IF to_regclass('auditoria.revinfo_seq') IS NOT NULL THEN
            RAISE EXCEPTION 'Existem duas sequencias revinfo_seq. Reconciliar antes de iniciar.';
        END IF;
        ALTER SEQUENCE public.revinfo_seq SET SCHEMA auditoria;
    END IF;
END $$
^^^
