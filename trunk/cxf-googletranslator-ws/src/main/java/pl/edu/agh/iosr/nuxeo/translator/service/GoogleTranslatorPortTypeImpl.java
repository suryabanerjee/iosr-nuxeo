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

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translator.DetectionRequest;
import pl.edu.agh.iosr.nuxeo.schema.translator.FileContentSource;
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
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultService;
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

//TODO faulty

	private Operation[] supportedOperations={Operation.TRANSLATION,Operation.LANGUAGE_DETECTION};
	private SourceType[] supportedSourceTypes={SourceType.STRING,SourceType.FILE};
	private FileFormat[] supportedSourceFileFormats={FileFormat.PLAIN_TEXT};
	
	private SupportedLanguagesManager supportedLanguagesManager=new SupportedLanguagesManager();
	
	

	@Override
	public String translate(TranslationRequest request) {		//TODO splitting, to result and void instead of String
		Translate.setHttpReferrer("localhost");		
		if(request.getContentSource().getSourceType()==SourceType.STRING){
			StringContentSource content=(StringContentSource)request.getContentSource();
			try {
				String translatedText=Translate.execute(content.getText(), Language.ENGLISH ,Language.POLISH);
			} catch (Exception e) {
				e.printStackTrace();
				return "TRANSLATION_FAILED";
			}
		   File file = new File("testfile.txt");
	    
	     /*   StringResultRequestWrapper s=new StringResultRequestWrapper();
	        s.setText("TAKI TEKST");
	        s.setTranslationRequestID("ala");
	        port.sendStringResult(s);*/	
		}
		if(request.getContentSource().getSourceType()==SourceType.FILE){
			try {
				FileContentSource fileContentSource=(FileContentSource)request.getContentSource();

				File sourceFile=createFileFromDataHandler(fileContentSource.getFile());					
				XliffParser xliffParser=new XliffParser(sourceFile);
				Map<String, String> sourceText;
				
				sourceText = xliffParser.getSourceText();
				Map<String,String> translatedText=new HashMap<String, String>();
				for(Map.Entry<String,String> unit:sourceText.entrySet()){
					String translatedUnitText=Translate.execute(unit.getValue(), Language.ENGLISH ,Language.POLISH);
					translatedText.put(unit.getKey(), translatedUnitText);
				}
				File resultFile=xliffParser.createXliffWithTranslation(translatedText, Language.POLISH.toString(),"result");
				
				TranslationResultService service = new TranslationResultService();
			    TranslationResultPortType port = service.getTranslationResultPort();
		        sendFile(port,resultFile);
		        sourceFile.delete();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		
		return "NOTHING";
	}



	@Override
	public String detectLanguage(DetectionRequest parameter) {
		if(parameter.getContentSource().getSourceType()==SourceType.STRING){
			StringContentSource content=(StringContentSource)parameter.getContentSource();
			return content.getText()+"DETECTED";
		}
		return "DETECTED";
	}

	
	@Override
	public Operations getSupportedOperations(Object parameter) {
		Operations operations=new Operations();
		operations.getOperations().addAll(Arrays.asList(supportedOperations));
		return operations;	
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
		fileFormats.getFileFormats().addAll(Arrays.asList(supportedSourceFileFormats));
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
		return null;
	}

	@Override
	public boolean ping(Object parameter) {
		return true;
	}

    public static void sendFile(TranslationResultPortType port, File file) {
    	
    	DataHandler dh = new DataHandler(new FileDataSource(file));
    	
    	FileResultRequestWrapper fileResult = new FileResultRequestWrapper();
    	fileResult.setFile(dh);
    	fileResult.setTranslationRequestID("123ABC");
    	port.sendFileResult(fileResult);
    	
    }
    
	private File createFileFromDataHandler(DataHandler dataHandler) throws IOException {
		String fileName=generateFileName();
		(new File(fileName)).createNewFile();
		File file=new File(fileName);
		FileOutputStream out = new FileOutputStream(file);
		dataHandler.writeTo(out); 
		return file;
	}
    
    private String generateFileName(){
    	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyy-hhmmss.SSS");
    	return "file-"+simpleDateFormat.format( new Date())+".txt";
    }
    


	
}