package com.github.mrm1st3r.libdroid.collections;


/**
 * Basic array utilities.
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.1
 */
public final class ArrayUtil {

	/**
	 * Hidden constructor for utility class.
	 * @throws InstantiationException There shall be no objects of this class
	 */
	private ArrayUtil() throws InstantiationException {
		throw new InstantiationException("There shall be no object");
	}

	/**
	 * Find an object inside an array.
	 * @param array haystack
	 * @param obj needle
	 * @return index of needle
	 */
	public static int getIndex(final Object[] array, final Object obj) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(obj)) {
				return i;
			}
		}
		return -1;
	}
}
