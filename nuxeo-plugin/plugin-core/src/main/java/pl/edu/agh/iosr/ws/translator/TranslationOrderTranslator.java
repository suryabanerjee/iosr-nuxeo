package pl.edu.agh.iosr.ws.translator;

import static pl.edu.agh.iosr.util.IosrLogger.log;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.swing.SortOrder;

import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translator.CallbackEndpoint;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileContentSource;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceType;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationRequest;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;

/**
 * Klasa tlumaczace TranslationOrder uzywany wewnatrz aplikacji na TranslationRequest
 * bedacy argumentem operacji tlumaczenia webservicu
 * Motywacja taka jak dla ResponseTranslator
 * @author lewickitom
 * */
public class TranslationOrderTranslator {
	
	
	
	public TranslationRequest translate(TranslationOrder translationOrder){
		
		TranslationRequest translationRequest=new TranslationRequest();
	
		translationRequest.setSourceLanguage(translationOrder.getLangPair().getFromLang());
		translationRequest.setDestinationLanguage(translationOrder.getLangPair().getToLang());
		
		
		String xliffName=translationOrder.getXliff();
		log(this.getClass(), "file got from translation order:"+xliffName);
		File xliffFile=new File(xliffName);
		FileContentSource fileContentSource=new FileContentSource();
		fileContentSource.setSourceType(SourceType.FILE);
		DataHandler dh = new DataHandler(new FileDataSource(xliffFile));
		
		fileContentSource.setFile(dh);
		translationRequest.setContentSource(fileContentSource);
	
		translationRequest.setTranslationRequestID(translationOrder.getRequestId().toString());
		
		CallbackEndpoint ce=new CallbackEndpoint();
    	ce.setEndpointURI("http://localhost:8080/cxf-translation-result-ws/services/result");
    	translationRequest.setCallbackEndpoint(ce);
		
		return translationRequest;
	
	}
	

	

}
