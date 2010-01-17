package pl.edu.agh.iosr.nuxeo.translator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.xml.sax.SAXException;

import com.google.api.detect.Detect;
import com.google.api.detect.DetectResult;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.TranslationStatus;
import pl.edu.agh.iosr.nuxeo.schema.translator.DetectionRequest;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileContentSource;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileFormats;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePair;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePairs;
import pl.edu.agh.iosr.nuxeo.schema.translator.Languages;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operation;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operations;
import pl.edu.agh.iosr.nuxeo.schema.translator.Option;
import pl.edu.agh.iosr.nuxeo.schema.translator.Options;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceType;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceTypes;
import pl.edu.agh.iosr.nuxeo.schema.translator.StringContentSource;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslateRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQualities;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQuality;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationRequest;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultService;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.ContentExtractionException;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.DetectionException;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;


/**
 * Implementacja WebSerwisu tlumaczacego dla Google Translator
 * @author lewickitom
 * */
@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/Translator.wsdl", 
        portName="TranslatorPort",
        serviceName="GoogleTranslatorService",
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType")
public class GoogleTranslatorPortTypeImpl implements TranslatorPortType{

//TODO change 'parameter' in wsdl to something more meaningful

	private TranslationResultService service;
	private TranslationResultPortType port;
	
	public static final String REFERRER="localhost";
	public static final int DETECTION_SAMPLE_SIZE_LIMIT=500;
	
	private Operation[] supportedOperations={Operation.TRANSLATION,Operation.LANGUAGE_DETECTION};
	private Option[] supportedOptions={};
	private TranslationQuality[] supportedQualities={};	
	private SourceType[] supportedSourceTypes={SourceType.STRING,SourceType.FILE};
	private String[] supportedSourceFileFormats={"application/x-xliff+xml"};
	
	private SupportedLanguagesManager supportedLanguagesManager=new SupportedLanguagesManager();
		
	@Override
	public void translate(TranslationRequest request){	
		System.out.println("traalala");
		
		service = new TranslationResultService();			
	    port = service.getTranslationResultPort();
	    Translate.setHttpReferrer(REFERRER);			
	    setTranslationResultEndpoint(request.getCallbackEndpoint().getEndpointURI());

		if(request.getContentSource().getSourceType()==SourceType.STRING){
			try {
				translateString(request);
			} catch (Exception e) {
				e.printStackTrace();
				sendStatus(TranslationStatus.FAILED,request.getTranslationRequestID());
			}
		}
		if(request.getContentSource().getSourceType()==SourceType.FILE){			
			try {
				System.out.println("BEFORE TRANSLATE FILE");
				translateFile(request);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("EXCEPTION!");
				sendStatus(TranslationStatus.FAILED,request.getTranslationRequestID());
			}
				
		}
	
		
	}
	
	private void translateString(TranslationRequest request) throws Exception {
		
		System.out.println("translate string");
		StringContentSource content=(StringContentSource)request.getContentSource();
		String translatedText=Translate.execute(content.getText(), Language.fromString(request.getSourceLanguage()) ,Language.fromString(request.getDestinationLanguage()));
		sendResultString(translatedText,request.getTranslationRequestID());
		
	}

	private void translateFile(TranslationRequest request) throws IOException,	
			ParserConfigurationException, SAXException, Exception,
			TransformerException, TransformerConfigurationException {
/*		System.out.println("translate file");
		FileContentSource fileContentSource=(FileContentSource)request.getContentSource();
		File sourceFile=createFileFromDataHandler(fileContentSource.getFile());					
		XliffParser xliffParser=new XliffParser(sourceFile);
		Map<String, String> sourceText=xliffParser.getSourceText();
		Map<String,String> translatedText=new HashMap<String, String>();
		for(Map.Entry<String,String> unit:sourceText.entrySet()){

			String translatedUnitText=Translate.execute(unit.getValue(),Language.fromString(request.getSourceLanguage()) ,Language.fromString(request.getDestinationLanguage()));
		
			translatedText.put(unit.getKey(), translatedUnitText);
		}
		File resultFile=xliffParser.createXliffWithTranslation(translatedText, request.getDestinationLanguage()+"_"+request.getDestinationLanguage().toUpperCase(),"result");
		sendResultFile(resultFile,request.getTranslationRequestID());
	*/
		FileContentSource fileContentSource=(FileContentSource)request.getContentSource();
		File sourceFile=createFileFromDataHandler(fileContentSource.getFile());	
		System.out.println("sourceFile length"+sourceFile.length());
		System.out.println("FILE CREATED");
		File resultFile=new XliffFileBatchModeTranslator().translateFile(sourceFile,request.getSourceLanguage(),request.getDestinationLanguage());
		System.out.println("AFTER BATCH TRANSLATION");
		System.out.println("SENDING RESULT FILE");
		sendResultFile(resultFile,request.getTranslationRequestID());
	//	sourceFile.delete();
		
	}



	@Override
	public String detectLanguage(DetectionRequest request) throws DetectionException, ContentExtractionException{
		
		Detect.setHttpReferrer(REFERRER);
		DetectResult detectResult=null;
		if(request.getContentSource().getSourceType()==SourceType.STRING){
			detectResult=detectFromString(request);		
		}
		else if(request.getContentSource().getSourceType()==SourceType.FILE){
			detectResult=detectFromFile(request);
		}
		else{
			throw  new ContentExtractionException("Not supported source type");
		}
		
		if(notDetected(detectResult))
			return null;
		else
			return detectResult.getLanguage().toString();
		
		//return null;
	}

	private DetectResult detectFromFile(DetectionRequest request) throws ContentExtractionException,DetectionException{

		FileContentSource content=(FileContentSource)request.getContentSource();
		XliffParser xliffParser=null;
		String sample=null;
		try {
			xliffParser=new XliffParser(createFileFromDataHandler(content.getFile()));
			sample=xliffParser.getSample(DETECTION_SAMPLE_SIZE_LIMIT);	
		} catch (IOException e) {
			throw new ContentExtractionException(e.getMessage(),e);
		} catch (ParserConfigurationException e) {
			throw new ContentExtractionException(e.getMessage(),e);
		} catch (SAXException e) {
			throw new ContentExtractionException(e.getMessage(),e);
		} 
		if(sample.length()==0)
			throw new ContentExtractionException("No text found!");		
		try{	
			//DetectResult detectResult=new DetectResult(Language.ENGLISH, true, 1.0);//=Detect.execute(sample);
			return null;
		}catch (Exception e) {
			throw new DetectionException(e.getMessage(),e);	
		}
		
	}

	private DetectResult detectFromString(DetectionRequest parameter) throws ContentExtractionException,DetectionException{
		
		StringContentSource content=(StringContentSource)parameter.getContentSource();
		String text=content.getText();
		if(text==null || text.length()==0)
			throw new ContentExtractionException("No text found!");
		
		try{
			DetectResult result=Detect.execute(content.getText());	
			return result;
		}
		catch(Exception e){
			throw new DetectionException(e.getMessage(),e);	
		}
		
	}

	
	private boolean notDetected(DetectResult result) {
		if(result.getLanguage()==Language.AUTO_DETECT)
			return true;
		else
			return false;
	}

	@Override
	public Operations getSupportedOperations(Object parameter) {
		
		Operations operations=new Operations();
		operations.getOperations().addAll(Arrays.asList(supportedOperations));
		return operations;	
	
	}
	@Override
	public Options getSupportedOptions(Object arg0) {
	
		Options options=new Options();
		options.getOptions().addAll(Arrays.asList(supportedOptions));
		return options;
	
	}
	@Override
	public SourceTypes getSupportedSourceTypes(Object parameter) {

		SourceTypes types=new SourceTypes();
		types.getSourceTypes().addAll(Arrays.asList(supportedSourceTypes));
		return types;
		
	}

	@Override
	public FileFormats getSupportedFileFormats(Object parameter) {
		
		FileFormats fileFormats=new FileFormats();
		fileFormats.getMimeTypes().addAll(Arrays.asList(supportedSourceFileFormats));
		return fileFormats;
		
	}
	
	@Override
	public Languages getSupportedDetections(Object parameter) {

		return supportedLanguagesManager.getDetectionSupportedLangugagesList();

	}
	


	@Override
	public LanguagePairs getSupportedTranslations(Object parameter) {

		return supportedLanguagesManager.getSupportedLangugagePairsList();

	}

	@Override
	public TranslationQualities getSupportedQualities(Object parameter) {
		
		TranslationQualities qualities=new TranslationQualities();
		qualities.getTranslationQualities().addAll(Arrays.asList(supportedQualities));
		return qualities;
		
	}

	@Override
	public boolean ping(Object parameter) {
		
		return true;
		
	}

//TODO kandydat na helpera	

	private File createFileFromDataHandler(DataHandler dataHandler) throws IOException {
		if(dataHandler==null)
			System.out.println("DH NULL!");
		String fileName=generateFileName();
		(new File(fileName)).createNewFile();
		File file=new File(fileName);
		FileOutputStream out = new FileOutputStream(file);
		dataHandler.writeTo(out); 
		return file;
		
	}
    
    private String generateFileName(){
    	
    	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyy-hhmmss.SSS");
    	return "file-"+simpleDateFormat.format( new Date())+".txt";				//TODO the same ms request problem
    	
    }

    
    
    private void sendResultFile( File file, String translationRequestId) {
    	
    	DataHandler dh = new DataHandler(new FileDataSource(file));
    	FileResultRequestWrapper fileResult = new FileResultRequestWrapper();
    	fileResult.setFile(dh);
    	fileResult.setTranslationRequestID(translationRequestId);
    	authenticate();
    	port.sendFileResult(fileResult);
    	
    }

	private void sendResultString(String translatedText,String translationRequestID) {

		StringResultRequestWrapper stringResult=new StringResultRequestWrapper();
		stringResult.setText(translatedText);
		stringResult.setTranslationRequestID(translationRequestID);
	//	port.sendStringResult(stringResult);
		
	}
	
	private void sendStatus(TranslationStatus status,String requestId){

		StatusRequestWrapper statusRequestWrapper=new StatusRequestWrapper();
		statusRequestWrapper.setStatus(status);
		statusRequestWrapper.setTranslationRequestID(requestId);
		authenticate();
		port.sendStatus(statusRequestWrapper);

	}
	
	private void authenticate(){
	
		Client client = ClientProxy.getClient(port);
        Endpoint cxfEndpoint = client.getEndpoint();
    	
    	Map<String,Object> outProps= new HashMap<String,Object>();
		
		outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		outProps.put(WSHandlerConstants.USER, "nuxeo");
		outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		//properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
		outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientPasswordHandler.class.getName());
		
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
		cxfEndpoint.getOutInterceptors().add(wssOut);
		
	}

    
	private void setTranslationResultEndpoint(String translationServiceEndpoint){
		
		BindingProvider bp=(BindingProvider)port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, translationServiceEndpoint);
   	
	}

	
}