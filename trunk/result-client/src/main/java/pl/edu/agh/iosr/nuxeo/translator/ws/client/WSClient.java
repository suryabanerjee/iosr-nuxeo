package pl.edu.agh.iosr.nuxeo.translator.ws.client;


import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultService;

public class WSClient {
	public static void main (String[] args){
        TranslationResultService service = new TranslationResultService();
        TranslationResultPortType port = service.getTranslationResultPort();
        
        File file = new File("testfile.txt");
        sendFile(port,file);
    }
    
    public static void sendFile(TranslationResultPortType port, File file) {
    	
    	DataHandler dh = new DataHandler(new FileDataSource(file));
    	
    	FileResultRequestWrapper fileResult = new FileResultRequestWrapper();
    	fileResult.setFile(dh);
    	fileResult.setTranslationRequestID("123ABC");
    	
    	port.sendFileResult(fileResult);
    }
}
