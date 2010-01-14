package pl.edu.agh.iosr.ws.translator;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Operation;
import pl.edu.agh.iosr.model.TranslationOption;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePair;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePairs;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operations;
import pl.edu.agh.iosr.nuxeo.schema.translator.Option;
import pl.edu.agh.iosr.nuxeo.schema.translator.Options;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceType;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceTypes;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQualities;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationQuality;


/**
 * Klasa tlumaczace odpowiedzi z webservicu na model danych uzywany w aplikacji
 * Jest to konieczne gdyz  typy danych uzywane przez webservicy i aplikacje nie sa takie same,
 * Chcemy by wszelkie zmiany definicji typow danych w wsdl serwisow tlumaczacych powodowaly zmiany tylko
 * w obrebie tej klasy. 
 * @author lewickitom
 * */

public class ResponseTranslator {

	/**
	 * Tlumaczenie listy operacji zwracanej przez webservice na odpowiednia liste operacji zgodna z typem Operation
	 * uzywanym wewnatrz aplikacji
	 * */
	public List<Operation> translateOperations(Operations operations){
	
		List<Operation> resultList=new ArrayList<Operation>();
		for(pl.edu.agh.iosr.nuxeo.schema.translator.Operation operation:operations.getOperations()){
			if(operation==pl.edu.agh.iosr.nuxeo.schema.translator.Operation.LANGUAGE_DETECTION){
				
				resultList.add(Operation.LANGUAGE_DETECTION);
			}
			else if(operation==pl.edu.agh.iosr.nuxeo.schema.translator.Operation.TRANSLATION){
				
				resultList.add(Operation.TRANSLATION);
				
			}
		}
		
		return resultList;
		
	}
	
	public List<TranslationOption> translateOptions(Options options) {
		
		List<TranslationOption> resultList=new ArrayList<TranslationOption>();
		for(Option option:options.getOptions()){

			if(option==Option.TRANSLATION_DEADLINE){
				resultList.add(TranslationOption.TRANSLATION_DEADLINE);
			}
			else if(option==Option.TRANSLATION_QUALITY){
				resultList.add(TranslationOption.TRANSLATION_QUALITY);
			}
			
		}
		return resultList;
		
	}
	

	public List<String> translateQualities(
			TranslationQualities translationQualities) {
		
		List<String> resultList=new ArrayList<String>();
		for(TranslationQuality translationQuality:translationQualities.getTranslationQualities()){
			resultList.add(translationQuality.toString());
		}
		return resultList;
		
	}

	public List<LangPair> translateLanguagePairs(LanguagePairs languagePairs) {
		
		List<LangPair> resultList=new ArrayList<LangPair>();
		for(LanguagePair languagePair:languagePairs.getLanguagePairs()){
			LangPair langPair=new LangPair();
			langPair.setFromLang(languagePair.getSrcLanguage());
			langPair.setToLang(languagePair.getDestLanguage());
			resultList.add(langPair);
		}
		return resultList;
		
	}

	public List<String> translateSourceTypes(SourceTypes sourceTypes) {
		
		List<String> resultList=new ArrayList<String>();
		for(SourceType srcType:sourceTypes.getSourceTypes())
			resultList.add(srcType.toString());
		return resultList;
		
	}

	
	
	

}
