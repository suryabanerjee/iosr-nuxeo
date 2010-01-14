package pl.edu.agh.iosr.nuxeo.translator.ws.client;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultService;

public class WSClient {

	
	public static void main (String[] args){
        TranslationResultService service = new TranslationResultService();
        TranslationResultPortType port = service.getTranslationResultPort();
        /*Client client = ClientProxy.getClient(port);
        Endpoint cxfEndpoint = client.getEndpoint();
        
    	Map<String,Object> outProps= new HashMap<String,Object>();
		
		outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		outProps.put(WSHandlerConstants.USER, "nuxeo");
		outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		//properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
		outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientPasswordHandler.class.getName());
		
		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
		cxfEndpoint.getOutInterceptors().add(wssOut);*/
        
        File file = new File("testfile.txt");
      
    	
    	DataHandler dh = new DataHandler(new FileDataSource(file));
    	
    	FileResultRequestWrapper fileResult = new FileResultRequestWrapper();
    	fileResult.setFile(dh);
    	fileResult.setTranslationRequestID("123");
    	
    	try{
    		port.sendFileResult(fileResult);
    	}
    	catch(SecurityException e){e.printStackTrace();}
    	
    	System.out.println("poszlo");
    }
}
