package br.com.portalincluir.controller;
import br.com.portalincluir.dto.request.RaiRequest;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.service.RaiDocumentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.env.MockEnvironment;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;
import javax.xml.parsers.DocumentBuilderFactory;
import static org.junit.jupiter.api.Assertions.*;
class RaiModeloDocumentoTest {
 private Map<String,byte[]> partes(byte[] doc) throws Exception { var m=new LinkedHashMap<String,byte[]>();try(var z=new ZipInputStream(new ByteArrayInputStream(doc))){ZipEntry e;while((e=z.getNextEntry())!=null)m.put(e.getName(),z.readAllBytes());}return m; }
 @Test void preservaModeloEPreencheTodosOsCampos() throws Exception {
  var d=new HashMap<String,Object>();
  for(var k:List.of("campus","componenteCurricular","professor","anoLetivo","periodoLetivo","periodicidade","numeroPeriodo","deficiencia","acompanhamento","comunicacao","aprendizagem","saude","competencias","objetivosGerais","objetivosEspecificos","comentarios","avaliacao","encaminhamentos","consideracoesGerais","local","data"))d.put(k,"");
  d.put("campus","Monteiro");d.put("anoLetivo","2026");d.put("periodicidade","BIMESTRAL");d.put("numeroPeriodo","3");d.put("professor","Docente de teste");d.put("data","2026-11-17");d.put("local","Monteiro");d.put("componenteCurricular","Matemática");
  d.put("deficiencia","Necessidade registrada na anamnese");d.put("acompanhamento","Acompanhamento importado");d.put("comunicacao","Comunicação registrada no PEI");d.put("aprendizagem","Dificuldades registradas no PEI");d.put("saude","Informações registradas no Plano de Ação");
  d.put("competencias","Habilidades registradas e resultados revisados.");d.put("objetivosGerais","Objetivo geral do PEA.");d.put("objetivosEspecificos","Primeiro objetivo específico.\nSegundo objetivo específico.");d.put("comentarios","Comentários revisados pela coordenação.");d.put("avaliacao","Critérios registrados no PEA e revisados.");d.put("encaminhamentos","Intervenção registrada no Plano de Ação.");d.put("consideracoesGerais","Considerações gerais do período.");
  var h=new HashMap<String,String>();for(var k:List.of("atencao","processamento","comandos","curiosidade","assiduidade","interacao","participacao","grupo","perguntas"))h.put(k,"Às vezes");d.put("habilidades",h);d.put("adequacoes",List.of("Conteúdos","Objetivos","Métodos","Recursos","Temporalidade","Avaliação"));
  var dados=new ObjectMapper().convertValue(d,RaiRequest.Conteudo.class);var e=new Estudante();e.setNome("Estudante de teste");e.setCurso("Técnico Integrado em Informática");
  var service=new RaiDocumentoService(new MockEnvironment());byte[] finalDoc=service.word(dados,e);byte[] modelo=getClass().getResourceAsStream("/documentos/rai-discente.docx").readAllBytes();
  var antes=partes(modelo);var depois=partes(finalDoc);assertEquals(antes.keySet(),depois.keySet());
  for(var nome:antes.keySet())if(!nome.equals("word/document.xml"))assertArrayEquals(antes.get(nome),depois.get(nome),nome);
  var f=DocumentBuilderFactory.newInstance();f.setNamespaceAware(true);var a=f.newDocumentBuilder().parse(new ByteArrayInputStream(antes.get("word/document.xml")));var b=f.newDocumentBuilder().parse(new ByteArrayInputStream(depois.get("word/document.xml")));
  String ns="http://schemas.openxmlformats.org/wordprocessingml/2006/main";assertEquals(99,b.getElementsByTagNameNS(ns,"p").getLength());assertTrue(a.getElementsByTagNameNS(ns,"sectPr").item(0).isEqualNode(b.getElementsByTagNameNS(ns,"sectPr").item(0)));
  var propsA=a.getElementsByTagNameNS(ns,"pPr");var propsB=b.getElementsByTagNameNS(ns,"pPr");assertEquals(propsA.getLength(),propsB.getLength());for(int i=0;i<propsA.getLength();i++)assertTrue(propsA.item(i).isEqualNode(propsB.item(i)),"Propriedades do parágrafo "+i);
  String texto=b.getDocumentElement().getTextContent();for(var valor:List.of("Estudante de teste","Técnico Integrado em Informática","Matemática","Informações registradas no Plano de Ação","Considerações gerais do período","( X ) Às vezes","( X ) Recursos"))assertTrue(texto.contains(valor),valor);
  assertFalse(texto.contains("paralisia cerebral"));assertFalse(texto.contains("Tecnologia em Construção de Edifícios"));assertFalse(texto.contains("Apurado"));
  Files.createDirectories(Path.of("target"));Files.write(Path.of("target/rai-modelo-teste.docx"),finalDoc);
  if ("1".equals(System.getenv("RAI_TEST_PDF"))) Files.write(Path.of("target/rai-modelo-teste.pdf"),service.pdf(finalDoc));
 }
}
