package com.github.mrm1st3r.libdroid.display;

import android.content.res.Resources;

/**
 * This utility class contains methods used for displaying things.
 * @author Lukas 'mrm1st3r' Taake
 */
public final class DimensionUtil {

	/**
	 * Hidden constructor for utility class.
	 * @throws InstantiationException There shall be no objects of this class
	 */
	private DimensionUtil() throws InstantiationException  {
		throw new InstantiationException("There shall be no objects.");
	}

	/**
	 * Convert a density independent size to pixels.
	 * @param dp Density independent size
	 * @return representing number of pixels
	 */
	public static int dpToPx(final int dp) {
	    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	/**
	 * Convert a number of pixels into a density independent size.
	 * @param px Number of pixels
	 * @return representing density independent size
	 */
	public static int pxToDp(final int px) {
	    return (int) (px / Resources.getSystem().getDisplayMetrics().density);
	}
}
