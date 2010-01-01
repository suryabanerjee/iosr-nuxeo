package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.LangPair;
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
		wsd.setDescription("tlumaczneie 1");
		wsd.setEndpoint("http://www.goooooogle.com");
		wsd.setName("Google");
		wsd.setSupportedDocumentTypes(new String[] { "good", "bad" });
		remoteWSs.add(wsd);

		wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("pl", "en"));
		wsd.getSupportedLangPairs().add(new LangPair("jp", "pl"));
		wsd.getSupportedLangPairs().add(new LangPair("es", "ru"));
		wsd.getSupportedLangPairs().add(new LangPair("en", "pl"));
		wsd.setDescription("tlumaczneie czopyka");
		wsd.setEndpoint("http://www.czopyk.pl");
		wsd.setName("Czopsonopolis");
		wsd.setSupportedDocumentTypes(new String[] { "very bad", "very good" });
		remoteWSs.add(wsd);

	}

	public List<TranslationServiceDescription> getRemoteWSs() {
		return remoteWSs;
	}

}
