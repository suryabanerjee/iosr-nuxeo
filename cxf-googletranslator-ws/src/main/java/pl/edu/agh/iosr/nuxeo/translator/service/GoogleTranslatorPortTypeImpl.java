package pl.edu.agh.iosr.nuxeo.translator.service;

import java.util.Arrays;
import java.util.Locale;

import javax.jws.WebService;

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
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;

@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/Translator.wsdl", 
        portName="TranslatorPort",
     //   serviceName="TranslatorService", 
        serviceName="GoogleTranslatorService",
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType")
        
public class GoogleTranslatorPortTypeImpl implements TranslatorPortType{
//		

	@Override
	public String translate(TranslationRequest parameter) {
		if(parameter.getContentSource().getSourceType()==SourceType.STRING){
			StringContentSource content=(StringContentSource)parameter.getContentSource();
			return content.getText();
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
		operations.getOperations().add(Operation.LANGUAGE_DETECTION);
		operations.getOperations().add(Operation.TRANSLATION);
		return operations;	
	}

	@Override
	public Languages getSupportedDetections(Object parameter) {
		Languages languages=new Languages();
		languages.getLanguages().add(Locale.ENGLISH.toString());
		languages.getLanguages().add(Locale.GERMAN.toString());
		
		LanguagePair l2=new LanguagePair();
		return languages;
	}

	@Override
	public LanguagePairs getSupportedTranslations(Object parameter) {
		LanguagePairs pairs=new LanguagePairs();
		LanguagePair l1=new LanguagePair();
		LanguagePair l2=new LanguagePair();
		l1.setSrcLanguage(Locale.ENGLISH.toString());
		l1.setDestLanguage(Locale.GERMAN.toString());
		l1.setSrcLanguage(Locale.GERMAN.toString());
		l2.setDestLanguage(Locale.ENGLISH.toString());
		pairs.getLanguagePairs().add(l1);
		pairs.getLanguagePairs().add(l2);
		
		
		return pairs;
	}

	@Override
	public FileFormats getSupportedFileFormats(Object parameter) {
		
		FileFormats fileFormats=new FileFormats();
		fileFormats.getFileFormats().add(FileFormat.PLAIN_TEXT);
		return fileFormats;
		
	}

	@Override
	public TranslationQualities getSupportedQualities(Object parameter) {
		TranslationQualities qualities=new TranslationQualities();
		TranslationQuality quality1=new TranslationQuality ();
		TranslationQuality quality2=new TranslationQuality ();
		TranslationQuality quality3=new TranslationQuality ();
		quality1.setQualityName("LOW");
		quality1.setQualityName("MEDIUM");
		quality1.setQualityName("HIGH");
		qualities.getTranslationQualities().add(quality1);
		qualities.getTranslationQualities().add(quality2);
		qualities.getTranslationQualities().add(quality3);
		return qualities;
	}

	@Override
	public SourceTypes getSupportedSourceTypes(Object parameter) {
		SourceTypes types=new SourceTypes();
		types.getSourceTypes().add(SourceType.STRING);
		return types;
	}

	@Override
	public boolean ping(Object parameter) {
		return true;
	}




	
}