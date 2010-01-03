package pl.edu.agh.iosr.util;

import java.security.SecureRandom;

/**
 * Generuje ultra randomy
 * */
public class IosrRandomGenerator {

	private static SecureRandom secureRandom = new SecureRandom();
	
	/**
	 * @return ultrarandom random Long
	 * */
	public static Long nextLong() {
		return Math.abs(secureRandom.nextLong());
	}
	
}
