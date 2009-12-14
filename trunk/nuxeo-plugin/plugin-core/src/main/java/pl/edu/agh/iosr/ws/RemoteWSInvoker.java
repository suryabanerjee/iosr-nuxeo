package pl.edu.agh.iosr.ws;

import java.io.File;
import java.util.List;
import java.util.Locale;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Operation;
import pl.edu.agh.iosr.model.TranslationOption;
import pl.edu.agh.iosr.model.TranslationOrder;
import pl.edu.agh.iosr.model.TranslationServiceDescription;


/**
 * Interfejs dla klasy wywolujacej operacje na
 * zdalnym webservicie tlumaczacym
 * */

public interface RemoteWSInvoker {

	void traslateAsync(TranslationServiceDescription webservice,TranslationOrder request, File content);

	List<Operation> getSuppportedOperations(TranslationServiceDescription webservice);
	
	List<TranslationOption> getSuppportedTranslationOptions(TranslationServiceDescription webservice);
	
	List<LangPair> getSuppportedTranslations(TranslationServiceDescription webservice);
	
	List<Locale> getSuppportedTranslationsPerLanguage(TranslationServiceDescription webservice,Locale language);
	
	List<String> getSuppportedQualities(TranslationServiceDescription webservice);
	
	List<String> getSuppportedFileFormats(TranslationServiceDescription webservice);
	
	List<String> getSuppportedSourceTypes(TranslationServiceDescription webservice);
	
}
