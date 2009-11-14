package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.springframework.stereotype.Repository;

import pl.edu.agh.iosr.model.LangPair;
import pl.edu.agh.iosr.ws.RemoteWSDescription;


/**
 * klasa przechowuje konfigurację dostępnych serwisów
 * 
 * po zapoznaniu z mechanizmami persystencji nuxeo trzeba bedzie to utwalać
 * */
@Name("configurationStorage")
@Scope(ScopeType.APPLICATION)
public class ConfigurationStorage {

	private List<RemoteWSDescription> remoteWSs = 
		new LinkedList<RemoteWSDescription>();

	@PostConstruct
	public void init() {
		RemoteWSDescription wsd = new RemoteWSDescription("WebService1", "com.google", "słabe tłumaczenie googla");
		wsd.getSupportedTranslation().add(new LangPair("PL", "ENG"));
		wsd.getSupportedTranslation().add(new LangPair("ENG", "PL"));
		remoteWSs.add(wsd);
		
		wsd = new RemoteWSDescription("SuperTlumaczenia", "com.super.tlumaczenia", "super tłumaczenie");
		wsd.getSupportedTranslation().add(new LangPair("PL", "ES"));
		wsd.getSupportedTranslation().add(new LangPair("ES", "RU"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "JP"));
		remoteWSs.add(wsd);
		
		wsd = new RemoteWSDescription("SuperTlumaczeniadsa", "com", "super tł");
		wsd.getSupportedTranslation().add(new LangPair("PL", "ES"));
		wsd.getSupportedTranslation().add(new LangPair("ES", "RU"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "JP"));
		wsd.getSupportedTranslation().add(new LangPair("KT", "KO"));
		wsd.getSupportedTranslation().add(new LangPair("WW", "DP"));
		wsd.getSupportedTranslation().add(new LangPair("JSF", "IR"));
		remoteWSs.add(wsd);
		
		
		wsd = new RemoteWSDescription("Tlumaczenia", "tlumaczenia", " tłumaczenie");
		wsd.getSupportedTranslation().add(new LangPair("PL", "ES"));
		wsd.getSupportedTranslation().add(new LangPair("ES", "RU"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "JP"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "KO"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "DP"));
		wsd.getSupportedTranslation().add(new LangPair("RU", "IR"));
		remoteWSs.add(wsd);
	}

	public List<RemoteWSDescription> getRemoteWSs() {
		return remoteWSs;
	}
	
}
