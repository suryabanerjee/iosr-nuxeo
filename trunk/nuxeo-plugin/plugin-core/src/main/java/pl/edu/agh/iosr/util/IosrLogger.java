package pl.edu.agh.iosr.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Wrapper dla loggerów.
 * Pozwala poprzez 2 linijki kodu zmienić całe logowanie aplikacji.
 * 
 * używa się tak:
 * import static pl.edu.agh.iosr.util.IosrLogger.log;
 * 
 * A potem gdziekolwiek:
 * log(this.getClass(), "ten tekst pojawi się w logach");
 * 
 * Posiada cache loggerów.
 * */
public class IosrLogger {

	private static Map<Class<?>, Logger> loggersCache = 
		new HashMap<Class<?>, Logger>();
	
	public enum Level {
		FATAL, WARNING, INFO
	}
	
	private static java.util.logging.Level getConcreteLevel(IosrLogger.Level level) {
		switch (level) {
			case FATAL:
				return java.util.logging.Level.SEVERE;
			case WARNING:
				return java.util.logging.Level.WARNING;
			default:
				return java.util.logging.Level.INFO;
		}
	}
	
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
		loggersCache.get(c).log(getConcreteLevel(DEFAULT_IOSR_LOGGING_LEVEL), o.toString());		
	}
	
}
