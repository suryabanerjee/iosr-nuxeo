package pl.edu.agh.iosr.services;

import javax.ejb.Local;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.*;

@Local
public interface TranslationResultService{

	public String sendStatus(StatusRequestWrapper parameters);
	public String sendStringResult(StringResultRequestWrapper parameters);
	public String sendFileResult(FileResultRequestWrapper parameters);
	
}
