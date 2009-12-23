package pl.edu.agh.iosr.nuxeo.ws.result;

import java.io.*;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.services.*;


@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/TranslationResult.wsdl", 
        portName="TranslationResultPort",
        serviceName="TranslationResultService", 
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType")
public class TranslationResultPortTypeImpl implements TranslationResultPortType{


	//@Override
	public String sendStatus(StatusRequestWrapper parameters) {
		String testString = "ws result";
		try {

			Properties p = new Properties();

			p.put("java.naming.provider.url", "jnp://localhost:1099");

			InitialContext ic = new InitialContext(p);

			Object o = ic.lookup("nuxeo/translationResultServiceImpl/local");

			if (o != null) {

				testString = ((TranslationResultService) o).sendStatus(new StatusRequestWrapper());

			}

		}

		catch (NamingException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		return testString;
		
	}

	//@Override
	public String sendStringResult(StringResultRequestWrapper parameters) {
		System.out.println("<< TranslationResultWS >>" + parameters.getTranslationRequestID() + ": " + parameters.getText());
		return null;
	}

	//@Override
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
