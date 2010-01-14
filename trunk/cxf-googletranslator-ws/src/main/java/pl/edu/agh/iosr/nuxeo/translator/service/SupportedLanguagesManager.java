package pl.edu.agh.iosr.nuxeo.translator.service;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePair;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePairs;
import pl.edu.agh.iosr.nuxeo.schema.translator.Languages;

import com.google.api.translate.Language;

/**
 * SupportedLanguagesManager generuje liste obslugiwanych przez GoogleTranslator tlumaczen oraz rozpoznawanych jezykow
 * @author lewickitom
 * */ 
public class SupportedLanguagesManager {


	
	public LanguagePairs getSupportedLangugagePairsList(){
		
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
		LanguagePairs languagePairs=new LanguagePairs();
		languagePairs.getLanguagePairs().addAll(pairList);
		return languagePairs;
	}
	
	public Languages getDetectionSupportedLangugagesList(){
		
		List<String> pairList=new ArrayList<String>();
		for(Language l:Language.values()){
			if(l!=Language.AUTO_DETECT)
				pairList.add(l.toString());
		}
			
		Languages languages=new Languages();
		languages.getLanguages().addAll(pairList);
		return languages;
		
	}
	
	
	
	
}
