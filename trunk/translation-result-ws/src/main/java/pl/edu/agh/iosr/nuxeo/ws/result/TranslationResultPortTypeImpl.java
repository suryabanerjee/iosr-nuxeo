package pl.edu.agh.iosr.nuxeo.ws.result;

import java.io.*;

import javax.activation.DataHandler;
import javax.jws.WebService;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;


@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/TranslationResult.wsdl", 
        portName="TranslationResultPort",
        serviceName="TranslationResultService", 
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType")
public class TranslationResultPortTypeImpl implements TranslationResultPortType{


	@Override
	public String sendStatus(StatusRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendStringResult(StringResultRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendFileResult(FileResultRequestWrapper parameters) {
		
		try {
			DataHandler dh = parameters.getFile();
			
			File file = new File(parameters.getTranslationRequestID());
		    if(!file.exists())
		    	file.createNewFile();

	        FileOutputStream out = new FileOutputStream(file);
			
        	dh.writeTo(out);
        	out.flush();
        	out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
