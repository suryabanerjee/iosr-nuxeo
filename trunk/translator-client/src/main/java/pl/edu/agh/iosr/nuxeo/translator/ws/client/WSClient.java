package pl.edu.agh.iosr.nuxeo.translator.ws.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import pl.edu.agh.iosr.nuxeo.wsdl.translator.*;
import pl.edu.agh.iosr.nuxeo.schema.translator.CallbackEndpoint;
import pl.edu.agh.iosr.nuxeo.schema.translator.DetectionRequest;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileFormat;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileFormats;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePair;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePairs;
import pl.edu.agh.iosr.nuxeo.schema.translator.Languages;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operation;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operations;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceType;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceTypes;
import pl.edu.agh.iosr.nuxeo.schema.translator.StringContentSource;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslateRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQualities;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQuality;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationRequest;

public class WSClient {
	
	//RemoteWSAsyncInvoker
	

	
	
	private Service translationService=new GoogleTranslatorService();
	private TranslatorPortType port;
	
	WSClient()throws IllegalArgumentException{
		
		this.translationService=new GoogleTranslatorService();
		this.port=(TranslatorPortType)translationService.getPort(TranslatorPortType.class);
		
	
	}

	
	
	
	private void setTranslationServiceEndpoint(String translationServiceEndpoint){
		
		BindingProvider bp=(BindingProvider)port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, translationServiceEndpoint);
    	
	
	}


 
	public static void main (String[] args){
		WSClient wsClient=new WSClient();
	
		
		
	
		
        wsClient.translate("Welcome");
     /*   wsClient.detect("text to translate");
        wsClient.getSupportedOperations("text to translate");
        wsClient.getSupportedTranslations("text to translate");
        wsClient.getSupportedDetections("text to translate");
       
        wsClient.getSupportedSourceTypes("text to translate");
        wsClient.getSupportedFileTypes("text to translate");
        wsClient.getSupportedQualities("text to translate");
        wsClient.ping("text to translate");
       */ 
        
    } 
    
    public void translate(String content) {
    	  	
    	TranslationRequest translationRequest=new TranslationRequest();
    	CallbackEndpoint ce=new CallbackEndpoint();
    	ce.setEndpointURI("http://costam.com/");
    	StringContentSource scs=new StringContentSource();
    	
    	
    	scs.setSourceType(SourceType.STRING);
    	scs.setText(content);
      	translationRequest.setContentSource(scs);
    	translationRequest.setTranslationRequestID("1xcv2312dac32312dasd");
    	translationRequest.setSourceLanguage(Locale.ENGLISH.toString());
    	translationRequest.setDestinationLanguage(Locale.GERMAN.toString());
    	translationRequest.setCallbackEndpoint(ce);
    	
    	String resp = port.translate(translationRequest);
        System.out.println("The response is: " + resp);
    }
    
    public void detect(String content) {
    	
    	DetectionRequest detectionRequest=new DetectionRequest();
    	StringContentSource scs=new StringContentSource();
    
    	scs.setSourceType(SourceType.STRING);
    	scs.setText("Welcome");
    	detectionRequest.setContentSource(scs);
    	detectionRequest.setDetectionRequestID("1xcv2312dac32312dasd");
    	
    	String resp = port.detectLanguage(detectionRequest);
        System.out.println("The response is: " + resp);
    }
    
    public void getSupportedOperations(String content) {

    	Operations resp=port.getSupportedOperations("whatever");
    	List<Operation> list= resp.getOperations();
    	
        System.out.println("The response is: " + list.get(0).toString());
    }
    
    public void getSupportedTranslations(String content) {

    	LanguagePairs resp=port.getSupportedTranslations("whatever");
    	List<LanguagePair> list= resp.getLanguagePairs();
    	
        System.out.println("The response is: " + list.get(0).getSrcLanguage().toString());
    }
    public void getSupportedDetections( String content) {

    	Languages resp=port.getSupportedDetections("whatever");
    	List<String> list= resp.getLanguages();
    	
        System.out.println("The response is: " + list.get(0));
    }
    
    public void getSupportedSourceTypes(String content) {

    	SourceTypes resp=port.getSupportedSourceTypes("whatever");
    	List<SourceType> list= resp.getSourceTypes();	
        System.out.println("The response is: " + list.get(0));
    }
    
    public  void getSupportedFileTypes(String content) {

    	FileFormats resp=port.getSupportedFileFormats("whatever");
    	List<FileFormat> list= resp.getFileFormats();	
        System.out.println("The response is: " + list.get(0));
    }

    public void getSupportedQualities(String content) {

    	TranslationQualities resp=port.getSupportedQualities("whatever");
    	List<TranslationQuality> list= resp.getTranslationQualities();	
        System.out.println("The response is: " + list.get(0));
        
    }
    
    public void ping( String content) {

    	Boolean resp=port.ping("whatever");
        System.out.println("The response is: " + resp);
    }
    
}
