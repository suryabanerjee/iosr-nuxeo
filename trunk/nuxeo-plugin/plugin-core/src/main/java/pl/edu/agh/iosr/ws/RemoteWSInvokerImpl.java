package pl.edu.agh.iosr.ws;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Operation;
import pl.edu.agh.iosr.model.TranslationOption;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.GoogleTranslatorService;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;

@Name("remoteWSInvoker")
@Scope(ScopeType.APPLICATION)
public class RemoteWSInvokerImpl implements RemoteWSInvoker {

	
	private Service translationService=new GoogleTranslatorService();
	private TranslatorPortType port;
	
	
	public List<String> getSuppportedFileFormats(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Operation> getSuppportedOperations(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<String> getSuppportedQualities(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<String> getSuppportedSourceTypes(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<TranslationOption> getSuppportedTranslationOptions(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<LangPair> getSuppportedTranslations(
			TranslationServiceDescription webservice) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Locale> getSuppportedTranslationsPerLanguage(
			TranslationServiceDescription webservice, Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void traslateAsync(TranslationServiceDescription webservice,
			TranslationOrder request, File content) {
		
		
	}
	
	

}
