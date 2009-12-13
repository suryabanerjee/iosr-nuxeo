package pl.edu.agh.iosr.nuxeo.translator.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
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
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultService;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;

@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/Translator.wsdl", 
        portName="TranslatorPort",
        serviceName="GoogleTranslatorService",
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType")
        
public class GoogleTranslatorPortTypeImpl implements TranslatorPortType{
//		

	private Operation[] supportedOperations={Operation.TRANSLATION,Operation.LANGUAGE_DETECTION};
	private SourceType[] supportedSourceTypes={SourceType.STRING};
	private FileFormat[] supportedSourceFileFormats={FileFormat.PLAIN_TEXT};
	private List<LanguagePair> supportedLangugagePairs;
	private List<String> detectionSupportedLangugages;
	
	
	private List<LanguagePair> getSupportedLangugagePairsList(){
		
		List<LanguagePair> pairList=new ArrayList<LanguagePair>();
		for(Language l1:Language.values()){
			for(Language l2:Language.values()){
				if(l1!=Language.AUTO_DETECT){
					if(l1!=l2 && l2!=Language.AUTO_DETECT){
						LanguagePair pair=new LanguagePair();
						pair.setSrcLanguage(l1.toString());
						pair.setDestLanguage(l2.toString());
						pairList.add(pair);
					}
				}
			}
		}
		return pairList;
	}
	
	private List<String> getDetectionSupportedLangugagesList(){
		List<String> pairList=new ArrayList<String>();
		for(Language l:Language.values()){
			if(l!=Language.AUTO_DETECT)
				pairList.add(l.toString());
		}
		return pairList;
		
	}
	
	@Override
	public String translate(TranslationRequest parameter) {
		Translate.setHttpReferrer("localhost");		
		if(parameter.getContentSource().getSourceType()==SourceType.STRING){
			StringContentSource content=(StringContentSource)parameter.getContentSource();
			try {
				String translatedText=Translate.execute(content.getText(), Language.ENGLISH ,Language.POLISH);
			} catch (Exception e) {
				e.printStackTrace();
				return "TRANSLATION_FAILED";
			}
	        TranslationResultService service = new TranslationResultService();
	        TranslationResultPortType port = service.getTranslationResultPort();
	   	
	        File file = new File("testfile.txt");
	        sendFile(port,file);
	     /*   StringResultRequestWrapper s=new StringResultRequestWrapper();
	        s.setText("TAKI TEKST");
	        s.setTranslationRequestID("ala");
	        port.sendStringResult(s);*/
	        

			
			
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
		Languages languages=new Languages();
		
	/*	languages.getLanguages().add(Locale.ENGLISH.toString());
		languages.getLanguages().add(Locale.GERMAN.toString());*/
		if(detectionSupportedLangugages==null){
			detectionSupportedLangugages=getDetectionSupportedLangugagesList();
		}
		languages.getLanguages().addAll(detectionSupportedLangugages);
		return languages;
	}
	


	@Override
	public LanguagePairs getSupportedTranslations(Object parameter) {
		/*
		LanguagePairs pairs=new LanguagePairs();
		LanguagePair l1=new LanguagePair();
		LanguagePair l2=new LanguagePair();
		l1.setSrcLanguage(Locale.ENGLISH.toString());
		l1.setDestLanguage(Locale.GERMAN.toString());
		l1.setSrcLanguage(Locale.GERMAN.toString());
		l2.setDestLanguage(Locale.ENGLISH.toString());
		pairs.getLanguagePairs().add(l1);
		pairs.getLanguagePairs().add(l2);
		*/
		LanguagePairs langs=new LanguagePairs();
		if(supportedLangugagePairs==null){
			supportedLangugagePairs=getSupportedLangugagePairsList();
		}
		langs.getLanguagePairs().addAll(supportedLangugagePairs);
		return langs;
	}



	@Override
	public TranslationQualities getSupportedQualities(Object parameter) {
/*		TranslationQualities qualities=new TranslationQualities();
		TranslationQuality quality1=new TranslationQuality ();
		TranslationQuality quality2=new TranslationQuality ();
		TranslationQuality quality3=new TranslationQuality ();
		quality1.setQualityName("LOW");
		quality1.setQualityName("MEDIUM");
		quality1.setQualityName("HIGH");
		qualities.getTranslationQualities().add(quality1);
		qualities.getTranslationQualities().add(quality2);
		qualities.getTranslationQualities().add(quality3);*/
		
		TranslationQualities qualities=new TranslationQualities();
		return qualities;
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


	
}