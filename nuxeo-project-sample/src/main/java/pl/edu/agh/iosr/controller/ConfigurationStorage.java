package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.model.TranslationService;


/**
 * na potrzeby testów GUI
 * */
@Name("configurationStorage")
@Scope(ScopeType.APPLICATION)
public class ConfigurationStorage {

	private List<TranslationService> remoteWSs = 
		new LinkedList<TranslationService>();

	@PostConstruct
	public void init() {
		TranslationService wsd = new TranslationService();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ENG"));
		wsd.getSupportedLangPairs().add(new LangPair("ENG", "PL"));
		wsd.setDescription("tlumaczneie 1");
		wsd.setEndpoint("http://www.goooooogle.com");
		wsd.setName("Google");
		remoteWSs.add(wsd);
		
		wsd = new TranslationService();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ES"));
		wsd.getSupportedLangPairs().add(new LangPair("ES", "RU"));
		wsd.getSupportedLangPairs().add(new LangPair("RU", "JP"));
		wsd.setDescription("tlumaczneie czopyka");
		wsd.setEndpoint("http://www.czopyk.pl");
		wsd.setName("Czopsonopolis");
		remoteWSs.add(wsd);
		
		wsd = new TranslationService();
		wsd.getSupportedLangPairs().add(new LangPair("PL", "ES"));
		wsd.getSupportedLangPairs().add(new LangPair("ES", "RU"));
		wsd.getSupportedLangPairs().add(new LangPair("RU", "JP"));
		wsd.getSupportedLangPairs().add(new LangPair("KT", "KO"));
		wsd.getSupportedLangPairs().add(new LangPair("WW", "DP"));
		wsd.getSupportedLangPairs().add(new LangPair("JSF", "IR"));
		wsd.setDescription("tlumaczneie 3");
		wsd.setEndpoint("http://www.onet.com/tr.wsdl?");
		wsd.setName("Onet");
		remoteWSs.add(wsd);
	}

	public List<TranslationService> getRemoteWSs() {
		return remoteWSs;
	}
	
}
