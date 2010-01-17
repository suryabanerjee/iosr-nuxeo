package pl.edu.agh.iosr.ws;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.controller.Mediator;
import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Operation;
import pl.edu.agh.iosr.model.TranslationOption;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.nuxeo.schema.translator.DetectionRequest;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileFormats;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePairs;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operations;
import pl.edu.agh.iosr.nuxeo.schema.translator.Options;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceTypes;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQualities;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationRequest;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.ContentExtractionException;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.DetectionException;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.GoogleTranslatorService;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;
import pl.edu.agh.iosr.ws.translator.ResponseTranslator;
import pl.edu.agh.iosr.ws.translator.TranslationOrderTranslator;


/**
 * Implementacja interfejsu wywolujacego operacje na webservisach tlumaczacych,
 * klasa ta moze wywolywac operacje na wszystkich webservicach zgodnych z Translator.wsdl
 * @author lewickitom
 * */  
@Name("remoteWSInvoker")
@Scope(ScopeType.APPLICATION)
public class RemoteWSInvokerImpl implements RemoteWSInvoker {
	
	@In("#{mediator}")
	private Mediator mediator;
	
	private Service translationService=new GoogleTranslatorService();
	private ResponseTranslator responseTranslator=new ResponseTranslator();
	private TranslationOrderTranslator translationOrderTranslator=new TranslationOrderTranslator();
	private TranslatorPortType port;
	
	@Create
	public void init() {
		
		this.translationService=new GoogleTranslatorService();
		this.port=(TranslatorPortType)translationService.getPort(TranslatorPortType.class);	
	
	}

	
	private void setTranslationServiceEndpoint(String translationServiceEndpoint){
		
		BindingProvider bp=(BindingProvider)port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, translationServiceEndpoint);
    	
	}
	
	public List<String> getSuppportedFileFormats(
			TranslationServiceDescription webservice) {
		
		setTranslationServiceEndpoint(webservice.getEndpoint());
		FileFormats fileFormats=port.getSupportedFileFormats("whatever");
		return fileFormats.getMimeTypes();
		
	}

	
	public List<Operation> getSuppportedOperations(
			TranslationServiceDescription webservice) {
		
		setTranslationServiceEndpoint(webservice.getEndpoint());
		Operations supportedOperations=port.getSupportedOperations("whatever");	
		return responseTranslator.translateOperations(supportedOperations);
	
	}

	
	public List<String> getSuppportedQualities(
			TranslationServiceDescription webservice) {
		
		setTranslationServiceEndpoint(webservice.getEndpoint());
		TranslationQualities translationQualities=port.getSupportedQualities("whatever");
		return responseTranslator.translateQualities(translationQualities);
		
	}

	
	public List<String> getSuppportedSourceTypes(
			TranslationServiceDescription webservice) {

		setTranslationServiceEndpoint(webservice.getEndpoint());
		SourceTypes sourceTypes=port.getSupportedSourceTypes("whatever");
		return responseTranslator.translateSourceTypes(sourceTypes);
		
	}

	
	public List<TranslationOption> getSuppportedTranslationOptions(
			TranslationServiceDescription webservice) {
		
		setTranslationServiceEndpoint(webservice.getEndpoint());
		Options options=port.getSupportedOptions("whatever");
		return responseTranslator.translateOptions(options);
		
	}

	
	public List<LangPair> getSuppportedTranslations(
			TranslationServiceDescription webservice) {
		
		setTranslationServiceEndpoint(webservice.getEndpoint());
		LanguagePairs languagePairs=port.getSupportedTranslations("whatever");
		return responseTranslator.translateLanguagePairs(languagePairs);
		
	}

	
	public List<Locale> getSuppportedTranslationsPerLanguage(		
			TranslationServiceDescription webservice, Locale language) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void traslateAsync(TranslationServiceDescription webservice,
			TranslationOrder request, File content) {
		
		System.out.println("\n\n\nENDPOINT IS"+webservice.getEndpoint());
		setTranslationServiceEndpoint(webservice.getEndpoint());
	    TranslationRequest translationRequest=translationOrderTranslator.translate(request);
		log(this.getClass(), "\n\n invoking translate...\n\n");
		port.translate(translationRequest);
		log(this.getClass(), "\n\n TRANSLATE CALLED+OK\n\n");
		
	}
	

	public void testInvoke(TranslationServiceDescription webservice,
			TranslationOrder request, File content){
		
		File f;
	      f=new File("findme");
	      if(!f.exists()){
	    	  try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	  	if(mediator==null)		//tylko by przetestowac wstrzykiwanie
	  		log(this.getClass(), "\n\n MEDIATOR TO NULL\n\n");
	  	else
			log(this.getClass(), "\n\n MEDIATOR OK\n\n");
		  
	  	
	    request.setXliff(f.getAbsolutePath());
		TranslationRequest translationRequest=translationOrderTranslator.translate(request);
		//port.translate(translationRequest);
		SourceTypes sourceTypes=port.getSupportedSourceTypes(null);
		log(this.getClass(), "\n\n CALLED OK\n\n");
		log(this.getClass(), "\n\n "+sourceTypes.getSourceTypes().size() +"+OK\n\n");
		Options options=port.getSupportedOptions(null);
		log(this.getClass(), "\n\n OPTIONS CALLED OK\n\n");
		
		log(this.getClass(), "\n\n "+options.getOptions() +"+OK\n\n");
		port.translate(translationRequest);
		log(this.getClass(), "\n\n TRANSLATE CALLED+OK\n\n");
		
		
		DetectionRequest detectionRequest=new DetectionRequest();
		detectionRequest.setDetectionRequestID(request.getRequestId().toString());
		detectionRequest.setContentSource(translationRequest.getContentSource());
		String detectResult=null;
		try {
			detectResult=port.detectLanguage(detectionRequest);
		} catch (ContentExtractionException e) {
			log(this.getClass(), "\n\n FAULT MESSAGE RECEIVED\n\n");
			e.printStackTrace();
		} catch (DetectionException e) {
			log(this.getClass(), "\n\n FAULT MESSAGE RECEIVED\n\n");
			e.printStackTrace();
		}
		
		log(this.getClass(), "\n\n DETECT CALLED+OK result is: "+detectResult+"\n\n");
		
	}
	

	

}
