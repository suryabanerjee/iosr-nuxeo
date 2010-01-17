package pl.edu.agh.iosr.controller;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.*;
import pl.edu.agh.iosr.services.TranslationResultService;

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
	
	@In("#{mediator}")
	private Mediator mediator;

	public void sendStatus(StatusRequestWrapper parameters) {
		mediator.updateTranslationStatus(new Long(parameters.getTranslationRequestID()), parameters.getStatus());
	}

	public void sendStringResult(StringResultRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return;
	}

	public void sendFileResult(FileResultRequestWrapper parameters) {
		
		mediator.deliverTranslationResult(new Long(parameters.getTranslationRequestID()), parameters.getFile());	
		return;
	}

	
	public Mediator getMediator() {
		return mediator;
	}

	public void setMediator(Mediator mediator) {
		this.mediator = mediator;
	}
}
