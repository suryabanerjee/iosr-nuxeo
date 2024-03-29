package pl.edu.agh.iosr.nuxeo.ws.result;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.services.TranslationResultService;



/**
 * Implementacja WebSerwisu uzywana przez tlumaczy
 * do zwracania wyniku translacji lub informacji o postepie tlumaczenia
 * 
 * @author handzlikt
 * */

@WebService(targetNamespace = "http://agh.edu.pl/iosr/nuxeo/wsdl/TranslationResult.wsdl", 
        portName="TranslationResultPort",
        serviceName="TranslationResultService", 
        endpointInterface="pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType")
public class TranslationResultPortTypeImpl implements TranslationResultPortType{

	/**
	 * Metoda uaktualniajaca stan translacji
	 * Wywolywana przez tlumacza w celu informowania o postepie translacji
	 * */
	public void sendStatus(StatusRequestWrapper parameters) {
		
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/TranslationResultServiceImpl/local");
			if (o != null) {
				((TranslationResultService) o).sendStatus(parameters);
			}
		}

		catch (NamingException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		return;
		
	}
	
	/**
	 * Sluzy do przekazania wyniku translacji w postaci stringa (opakowanego w parametrze metody).
	 * Zwracana wartosc moze byc nullem w przypadku sukcesu lub komunikatem o bledzie
	 * 
	 * */
	public void sendStringResult(StringResultRequestWrapper parameters) {
		
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/TranslationResultServiceImpl/local");
			if (o != null) {
				((TranslationResultService) o).sendStringResult(parameters);
			}
		}

		catch (NamingException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		return;
	}

	
	/**
	 * Sluzy do przekazania wyniku translacji w postaci pliku (opakowanego w parametrze metody).
	 * Zwracana wartosc moze byc nullem w przypadku sukcesu lub komunikatem o bledzie
	 * 
	 * */
	public void sendFileResult(FileResultRequestWrapper parameters) {
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/TranslationResultServiceImpl/local");
			if (o != null) {
				((TranslationResultService) o).sendFileResult(parameters);
			}
		}

		catch (NamingException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return;
		
	}


}
