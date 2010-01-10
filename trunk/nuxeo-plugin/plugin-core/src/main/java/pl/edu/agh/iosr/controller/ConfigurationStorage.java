package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.Quality;
import pl.edu.agh.iosr.model.TranslationServiceDescription;
import pl.edu.agh.iosr.persistence.CoreSessionProxy;

/**
 * na potrzeby testów GUI
 * */
@Name("configurationStorage")
@Scope(ScopeType.APPLICATION)
public class ConfigurationStorage {

	private List<TranslationServiceDescription> remoteWSs = new LinkedList<TranslationServiceDescription>();

	@In(create = true)
	CoreSessionProxy coreSessionProxy;

	@Create
	public void init() {
		TranslationServiceDescription wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("pl", "en"));
		wsd.getSupportedLangPairs().add(new LangPair("en", "pl"));
		wsd.setDescription("abc");
		wsd.setEndpoint("http://localhost:9080/cxf-translation-result-ws/services/result");
		wsd.setName("Google");
		wsd.getSupportedQualities().add(new Quality("good"));
		wsd.getSupportedQualities().add(new Quality("bad"));
		remoteWSs.add(wsd);
	}

	public List<TranslationServiceDescription> getRemoteWSs() {
		return remoteWSs;
	}

}
