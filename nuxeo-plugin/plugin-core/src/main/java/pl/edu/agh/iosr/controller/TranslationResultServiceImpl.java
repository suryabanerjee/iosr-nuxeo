package pl.edu.agh.iosr.controller;

import javax.ejb.Stateless;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.io.*;
import javax.activation.DataHandler;

import pl.edu.agh.iosr.services.TranslationResultService;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.*;

/**
 * Implementacja interefejs sluzacego do komunikacji webserwisu z pluginem nuxeo 
 * Niezbedny do zwrocenia wyniku translacji lub informacji o postepie tlumaczenia
 * 
 * @author handzlikt
 * */

@Name("translationResultServiceImpl")
@Scope(ScopeType.APPLICATION)
@Stateless
public class TranslationResultServiceImpl implements TranslationResultService {
	
	private Mediator mediator;

	public void sendStatus(StatusRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return;
	}

	public void sendStringResult(StringResultRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return;
	}

	public void sendFileResult(FileResultRequestWrapper parameters) {
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
		
		return;
	}

	
	public Mediator getMediator() {
		return mediator;
	}

	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
}
