package pl.edu.agh.iosr.nuxeo.translator.ws.client;

import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translator.TranslatorService;
import pl.edu.agh.iosr.nuxeo.schema.translator.TranslateRequestWrapper;

public class WSClient {
	public static void main (String[] args){
        TranslatorService service = new TranslatorService();
        TranslatorPortType port = service.getTranslatorPort();           

        translate(port, "text to translate");
    } 
    
    public static void translate(TranslatorPortType port, String content) {
    	String resp = null;

    	TranslateRequestWrapper parameters = new TranslateRequestWrapper();
    	parameters.setContent(content);
    	resp = port.translate(parameters);
    	
        System.out.println("The response is: " + resp);
    }
}
