package pl.edu.agh.iosr.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

/**
 * klasa przechowuje konfigurację dostępnych serwisów
 * bean springowy
 * */
public class ConfigurationStorage {

	private List<RemoteWSDescription> remoteWSs = 
		new LinkedList<RemoteWSDescription>();
	
	
	public ConfigurationStorage() {
		init();
	}
	/**
	 * Na razie wpisuje tu jakies przykladowe dane,
	 * �eby mialo mi co gui pokazywac
	 * 
	 * POSTCONSTRUCT nie daje rady w springu!
	 * */
	@PostConstruct
	public void init() {
		remoteWSs.add(new RemoteWSDescription("WebService1", "com.google"));
		remoteWSs.add(new RemoteWSDescription("SuperTlumaczenia", "com.super.tlumaczenia"));
	}

	public List<RemoteWSDescription> getRemoteWSs() {
		return remoteWSs;
	}
	
}
