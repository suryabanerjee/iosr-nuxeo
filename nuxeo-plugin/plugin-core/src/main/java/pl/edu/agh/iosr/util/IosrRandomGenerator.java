package pl.edu.agh.iosr.util;

import java.security.SecureRandom;

/**
 * Generuje liczby losowe.
 * */
public class IosrRandomGenerator {

	private static SecureRandom secureRandom = new SecureRandom();
	
	/**
	 * @return Nieujemna liczba losowa.
	 * */
	public static Long nextLong() {
		return Math.abs(secureRandom.nextLong());
	}
	
}
