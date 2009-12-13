package pl.edu.agh.iosr.nuxeo.translator.ws.client;

import java.util.List;
import java.util.Locale;

import pl.edu.agh.iosr.nuxeo.schema.translator.FileFormat;
import pl.edu.agh.iosr.nuxeo.schema.translator.LanguagePair;
import pl.edu.agh.iosr.nuxeo.schema.translator.Operation;
import pl.edu.agh.iosr.nuxeo.schema.translator.SourceType;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslationRequest;

public interface RemoteWSInvoker {

	void traslateAsync(WSDescriptor webservice,TranslationRequest request);
	
	List<Operation> getSuppportedOperations(WSDescriptor webservice);
	
	List<LanguagePair> getSuppportedTranslations(WSDescriptor webservice);
	
	List<Locale> getSuppportedTranslationsPerLanguage(WSDescriptor webservice,Locale language);
	
	List<String> getSuppportedQualities(WSDescriptor webservice);
	
	List<FileFormat> getSuppportedFileFormats(WSDescriptor webservice);
	
	List<SourceType> getSuppportedSourceTypes(WSDescriptor webservice);
	
}
