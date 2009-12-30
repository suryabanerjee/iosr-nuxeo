package pl.edu.agh.iosr.nuxeo.ws.result;


import java.util.Properties;

import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.wsdl.translationresult.TranslationResultPortType;
import pl.edu.agh.iosr.services.*;



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
	//@Override
	public void sendStatus(StatusRequestWrapper parameters) {
		
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/translationResultServiceImpl/local");
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

	//@Override
	public void sendStringResult(StringResultRequestWrapper parameters) {
		
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/translationResultServiceImpl/local");
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
	//@Override
	public void sendFileResult(FileResultRequestWrapper parameters) {
		
		try {

			Properties p = new Properties();
			p.put("java.naming.provider.url", "jnp://localhost:1099");
			InitialContext ic = new InitialContext(p);
			Object o = ic.lookup("nuxeo/translationResultServiceImpl/local");
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
