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

	private List<TranslationServiceDescription> remoteWSs = 
		new LinkedList<TranslationServiceDescription>();

	@In(create=true)
	CoreSessionProxy coreSessionProxy;
	
	@Create
	public void init() {
		TranslationServiceDescription wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ENG"));
		wsd.getSupportedLangPairs().add(new LangPair("ENG", "PL"));
		wsd.setDescription("tlumaczneie 1");
		wsd.setEndpoint("http://www.goooooogle.com");
		wsd.setName("Google");
		wsd.getSupportedQualities().add("very good");
		wsd.getSupportedQualities().add("very very good");
		remoteWSs.add(wsd);
		
		wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ES"));
		wsd.getSupportedLangPairs().add(new LangPair("ES", "RU"));
		wsd.getSupportedLangPairs().add(new LangPair("RU", "JP"));
		wsd.setDescription("tlumaczneie czopyka");
		wsd.setEndpoint("http://www.czopyk.pl");
		wsd.setName("Czopsonopolis");
		wsd.getSupportedQualities().add("poor");
		wsd.getSupportedQualities().add("very very poor, but free");
		remoteWSs.add(wsd);
		
		wsd = new TranslationServiceDescription();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ES"));
		wsd.getSupportedLangPairs().add(new LangPair("ES", "RU"));
		wsd.getSupportedLangPairs().add(new LangPair("RU", "JP"));
		wsd.getSupportedLangPairs().add(new LangPair("KT", "KO"));
		wsd.getSupportedLangPairs().add(new LangPair("WW", "DP"));
		wsd.getSupportedLangPairs().add(new LangPair("JSF", "IR"));
		wsd.setDescription("tlumaczneie 3");
		wsd.setEndpoint("http://www.onet.com/tr.wsdl?");
		wsd.setName("Onet");
		wsd.setLanguageDetection(true);
		remoteWSs.add(wsd);
		
		coreSessionProxy.getCoreSession();
	}

	public List<TranslationServiceDescription> getRemoteWSs() {
		return remoteWSs;
	}
	
}
