package com.github.mrm1st3r.libdroid.io;

/**
 * This class provides additional functions for use with files.
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 *
 */
public final class FileUtil {
	/**
	 * List of characters that musn't be contained in a filename.
	 */
	public static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
		'\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

	/**
	 * Hidden constructor for utility class.
	 * @throws InstantiationException There shall be no objects of this class
	 */
	private FileUtil() throws InstantiationException {
		throw new InstantiationException("There shall be no object");
	}

	/**
	 * Validate a filename.
	 * @param filename Name to be validated
	 * @return Validation result
	 */
	public static boolean isValidName(final String filename) {
		if (filename.trim().length() == 0) {
			return false;
		}

		for (int i = 0; i < ILLEGAL_CHARACTERS.length; i++) {
			if (filename.contains("" + ILLEGAL_CHARACTERS[i])) {
				return false;
			}
		}
		
		return true;
	}
}
