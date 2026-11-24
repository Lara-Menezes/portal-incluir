package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.RaiRequest;
import br.com.portalincluir.model.Estudante;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.*;

@Service
public class RaiDocumentoService {
 private static final String NS="http://schemas.openxmlformats.org/wordprocessingml/2006/main";
 private final Environment ambiente;
 public RaiDocumentoService(Environment ambiente) { this.ambiente=ambiente; }
 public byte[] word(RaiRequest.Conteudo d, Estudante estudante) {
  try (var entrada=new ZipInputStream(new ClassPathResource("documentos/rai-discente.docx").getInputStream()); var saida=new ByteArrayOutputStream(); var zip=new ZipOutputStream(saida)) {
   ZipEntry parte;
   while ((parte=entrada.getNextEntry())!=null) {
    byte[] conteudo=entrada.readAllBytes();
    if (parte.getName().equals("word/document.xml")) conteudo=preencher(conteudo,d,estudante);
    zip.putNextEntry(new ZipEntry(parte.getName())); zip.write(conteudo); zip.closeEntry();
   }
   zip.finish(); return saida.toByteArray();
  } catch (Exception e) { throw new IllegalStateException("Não foi possível preencher o modelo institucional do RAI.",e); }
 }
 private byte[] preencher(byte[] fonte,RaiRequest.Conteudo d,Estudante estudante) throws Exception {
  var fabrica=DocumentBuilderFactory.newInstance(); fabrica.setNamespaceAware(true);
  fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
  fabrica.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD,""); fabrica.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA,"");
  var xml=fabrica.newDocumentBuilder().parse(new ByteArrayInputStream(fonte));
  var ps=xml.getElementsByTagNameNS(NS,"p");
  if(ps.getLength()!=99 || !ps.item(1).getTextContent().contains("RELATÓRIO DE ACOMPANHAMENTO DISCENTE")) throw new IllegalStateException("Estrutura do modelo alterada.");
  substituir(xml,ps,2,"CAMPUS "+d.campus().toUpperCase(Locale.forLanguageTag("pt-BR")));
  substituir(xml,ps,4,"CURSO: "+estudante.getCurso());
  substituir(xml,ps,5,"COMPONENTE CURRICULAR: "+(d.componenteCurricular().isBlank()?"Todos os componentes":d.componenteCurricular()));
  substituir(xml,ps,6,"PROFESSOR: "+d.professor()); substituir(xml,ps,7,"ESTUDANTE: "+estudante.getNome());
  substituir(xml,ps,8,"ANO LETIVO: "+d.anoLetivo());
  String periodo=d.periodicidade().equals("ANUAL")?"ANUAL":d.numeroPeriodo()+"º "+(d.periodicidade().equals("BIMESTRAL")?"BIMESTRE":"SEMESTRE");
  substituir(xml,ps,9,"PERÍODO LETIVO: "+periodo);
  substituir(xml,ps,12,"Deficiência: "+d.deficiencia());
  substituir(xml,ps,14,"Acompanhamento por profissional específico do NAPNE: "+d.acompanhamento());
  substituir(xml,ps,16,"Comunicação: "+d.comunicacao());
  substituir(xml,ps,18,"Déficit de Aprendizagem: "+d.aprendizagem());
  substituir(xml,ps,19,"Informações do Setor de Saúde: "+d.saude());
  var chaves=List.of("atencao","processamento","comandos","curiosidade","assiduidade","interacao","participacao","grupo","perguntas");
  var frequencias=List.of("Sempre","Às vezes","Raramente","Nunca");
  for(int i=0;i<chaves.size();i++) {
   int indice=26+i*3; String original=textos(ps.item(indice));
   var marcador=java.util.regex.Pattern.compile("\\([^)]*\\)").matcher(original); var resultado=new StringBuffer(); int opcao=0;
   while(marcador.find()) marcador.appendReplacement(resultado,java.util.regex.Matcher.quoteReplacement("("+(frequencias.get(opcao++).equals(d.habilidades().get(chaves.get(i)))?" X ":"   ")+")"));
   marcador.appendTail(resultado); substituir(xml,ps,indice,resultado.toString());
  }
  substituir(xml,ps,54,d.competencias()); substituir(xml,ps,61,d.objetivosGerais()); substituir(xml,ps,64,d.objetivosEspecificos());
  substituir(xml,ps,69,d.comentarios()); substituir(xml,ps,76,d.avaliacao());
  var adequacoes=List.of("Conteúdos","Objetivos","Métodos","Recursos","Temporalidade","Avaliação");
  for(int linha=0;linha<2;linha++) {
   String original=textos(ps.item(77+linha)); var marcador=java.util.regex.Pattern.compile("\\([^)]*\\)").matcher(original); var resultado=new StringBuffer(); int opcao=linha*3;
   while(marcador.find()) marcador.appendReplacement(resultado,java.util.regex.Matcher.quoteReplacement("("+(d.adequacoes().contains(adequacoes.get(opcao++))?" X ":"   ")+")"));
   marcador.appendTail(resultado); substituir(xml,ps,77+linha,resultado.toString());
  }
  substituir(xml,ps,82,d.encaminhamentos()); substituir(xml,ps,89,d.consideracoesGerais());
  String data=d.data().isBlank()?"_______ de _________________ de _________":LocalDate.parse(d.data()).format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' uuuu",Locale.forLanguageTag("pt-BR")));
  substituir(xml,ps,93,(d.local().isBlank()?"_________________________":d.local())+", "+data+".");
  substituir(xml,ps,98,"IFPB – CAMPUS "+d.campus().toUpperCase(Locale.forLanguageTag("pt-BR")));
  var tf=TransformerFactory.newInstance(); tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD,""); tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET,"");
  var transformador=tf.newTransformer(); transformador.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
  var saida=new ByteArrayOutputStream(); transformador.transform(new DOMSource(xml),new StreamResult(saida)); return saida.toByteArray();
 }
 private static String textos(Node p) { var ts=((Element)p).getElementsByTagNameNS(NS,"t"); var s=new StringBuilder(); for(int i=0;i<ts.getLength();i++) s.append(ts.item(i).getTextContent()); return s.toString(); }
 private static void substituir(Document xml,NodeList ps,int indice,String valor) {
  var p=(Element)ps.item(indice); var ts=p.getElementsByTagNameNS(NS,"t");
  for(int i=0;i<ts.getLength();i++) ts.item(i).setTextContent("");
  var runs=p.getElementsByTagNameNS(NS,"r"); Element run;
  if(runs.getLength()>0) run=(Element)runs.item(0); else { run=xml.createElementNS(NS,"w:r"); p.appendChild(run); }
  String[] linhas=valor.split("\\r?\\n",-1);
  for(int i=0;i<linhas.length;i++) { if(i>0) run.appendChild(xml.createElementNS(NS,"w:br")); var t=xml.createElementNS(NS,"w:t");t.setAttributeNS(javax.xml.XMLConstants.XML_NS_URI,"xml:space","preserve");t.setTextContent(linhas[i]);run.appendChild(t); }
 }
 public synchronized byte[] pdf(byte[] word) {
  Path pasta=null;
  try {
   pasta=Files.createTempDirectory("portal-rai-"); var arquivo=pasta.resolve("relatorio.docx"); Files.write(arquivo,word);
   boolean windows=System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
   String conversor=ambiente.getProperty("app.documentos.conversor",windows?"word":"libreoffice");
   ProcessBuilder comando;
   if(conversor.equals("word") && windows) {
    var script=pasta.resolve("converter.ps1"); try(var in=new ClassPathResource("documentos/word-para-pdf.ps1").getInputStream()) { Files.copy(in,script); }
    comando=new ProcessBuilder("powershell.exe","-NoProfile","-NonInteractive","-ExecutionPolicy","Bypass","-File",script.toString(),"-Entrada",arquivo.toString(),"-Saida",pasta.resolve("relatorio.pdf").toString());
   } else {
    var executavel=ambiente.getProperty("app.documentos.libreoffice","soffice");
    comando=new ProcessBuilder(executavel,"-env:UserInstallation="+pasta.resolve("perfil-lo").toUri(),"--headless","--convert-to","pdf","--outdir",pasta.toString(),arquivo.toString());
   }
   var processo=comando.redirectErrorStream(true).redirectOutput(pasta.resolve("conversao.log").toFile()).start();
   if(!processo.waitFor(90,TimeUnit.SECONDS)) { processo.destroyForcibly(); processo.waitFor(5,TimeUnit.SECONDS); throw new IOException("Tempo de conversão excedido."); }
   var pdf=pasta.resolve("relatorio.pdf");
   if(processo.exitValue()!=0 || !Files.exists(pdf)) throw new IOException("Conversor não produziu o PDF.");
   byte[] resultado=Files.readAllBytes(pdf);
   if(resultado.length<5 || !new String(resultado,0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-")) throw new IOException("PDF inválido.");
   return resultado;
  } catch(InterruptedException e) { Thread.currentThread().interrupt(); throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Conversão do RAI interrompida."); }
    catch(IOException e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Não foi possível converter o modelo Word para PDF. Verifique o conversor de documentos do servidor."); }
  finally { if(pasta!=null) try(var arquivos=Files.walk(pasta)) { arquivos.sorted(Comparator.reverseOrder()).forEach(p->{try { Files.deleteIfExists(p); } catch(IOException ignored) {}}); } catch(IOException ignored) {} }
 }
}
