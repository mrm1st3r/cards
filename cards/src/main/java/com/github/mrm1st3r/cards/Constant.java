package com.github.mrm1st3r.cards;

/**
 * This class contains constants that are used by the application but do not
 * belong to any particular class.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public final class Constant {

	/**
	 * Extra field for intents that contains the local player name.
	 */
	public static final String EXTRA_LOCAL_NAME = "EXTRA_LOCAL_NAME";
	
	/**
	 * Private constructor for utility class.
	 * @throws InstantiationException utility classes shouldn't have objects.
	 */
	private Constant() throws InstantiationException {
		throw new InstantiationException();
	}
}
