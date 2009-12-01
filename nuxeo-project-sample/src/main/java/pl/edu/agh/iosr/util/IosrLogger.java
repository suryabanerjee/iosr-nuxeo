package pl.edu.agh.iosr.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Używamy takiego zajebistego loggera,
 * 
 * Powód: 2 linijki, żeby zmienić całe logowanie aplikacji.
 * 
 * 
 * 
 * używa się tak:
 * import static pl.edu.agh.iosr.util.IosrLogger.log;
 * 
 * a potem gdziekolwiek:
 * log(this.getClass(), "coscoscos");
 * */
public class IosrLogger {

	private static Map<Class<?>, Logger> loggersCache = 
		new HashMap<Class<?>, Logger>();
	
	public static Level DEFAULT_IOSR_LOGGING_LEVEL = Level.WARNING;
	
	/**
	 * Loguje z domyślnym poziomem logowania
	 * */
	public static void log (Class<?> c, Object o) {
		log(c, o.toString(), DEFAULT_IOSR_LOGGING_LEVEL);
	}
	
	/**
	 * Loguje z zadanym poziomem logowania
	 * */
	public static void log (Class<?> c, Object o, Level level) {
		if (!loggersCache.containsKey(c)) {
			loggersCache.put(c, Logger.getLogger(c.getName()));
		}
		loggersCache.get(c).log(DEFAULT_IOSR_LOGGING_LEVEL, o.toString());		
	}
	
}
