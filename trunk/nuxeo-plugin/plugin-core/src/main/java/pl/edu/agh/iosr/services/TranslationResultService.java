package pl.edu.agh.iosr.services;

import javax.ejb.Local;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.*;

/**
 * Interefejs sluzacy do komunikacji webserwisu z pluginem nuxeo 
 * Niezbedny do zwrocenia wyniku translacji lub informacji o postepie tlumaczenia
 * 
 * @author handzlikt
 * */

@Local
public interface TranslationResultService{

	public void sendStatus(StatusRequestWrapper parameters);
	public void sendStringResult(StringResultRequestWrapper parameters);
	public void sendFileResult(FileResultRequestWrapper parameters);
	
}
