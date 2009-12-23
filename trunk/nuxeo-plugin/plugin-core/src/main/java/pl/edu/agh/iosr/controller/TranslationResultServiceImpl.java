package pl.edu.agh.iosr.controller;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.nuxeo.schema.translationresult.FileResultRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StatusRequestWrapper;
import pl.edu.agh.iosr.nuxeo.schema.translationresult.StringResultRequestWrapper;
import pl.edu.agh.iosr.services.TranslationResultService;

@Name("translationResultServiceImpl")
@Scope(ScopeType.APPLICATION)
@Stateless
public class TranslationResultServiceImpl implements TranslationResultService {

	public String sendStatus(StatusRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendStringResult(StringResultRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendFileResult(FileResultRequestWrapper parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
