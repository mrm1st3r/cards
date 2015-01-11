package com.github.mrm1st3r.util;

import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class provides additional functions for bitmap processing.
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public final class BitmapUtil {

	/**
	 * Buffer scaled bitmaps.
	 */
	private static HashMap<String, Bitmap> bitmapBuffer =
			new HashMap<String, Bitmap>();
	
	/**
	 * @throws InstantiationException There should be no instances of this class
	 */
	private BitmapUtil() throws InstantiationException {
		throw new InstantiationException();
	}

	/**
	 * Load a bitmap in a given size.
	 * @param res Resource set to use
	 * @param resId Bitmap to load
	 * @param reqWidth Width to load
	 * @param reqHeight Height to load
	 * @return The requested bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(
			final Resources res, final int resId,
			final int reqWidth, final int reqHeight) {
		
		String bufferKey = resId + "_" + reqWidth + "_" + reqHeight;
		
		if (bitmapBuffer.containsKey(bufferKey)) {
			return bitmapBuffer.get(bufferKey);
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize =
				calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		Bitmap tmp = BitmapFactory.decodeResource(res, resId, options);
		bitmapBuffer.put(bufferKey, tmp);
		return tmp;
	}

	/**
	 * Calculate the sample size for a bitmap.
	 * @param options Option set
	 * @param reqWidth Width to scale to
	 * @param reqHeight Height to scale to
	 * @return Sample factor
	 */
	public static int calculateInSampleSize(
			final BitmapFactory.Options options,
			final int reqWidth, final int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both height and width larger than
			// the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * Get the identifier of an image.
	 * @param context Application context
	 * @param filename The images filename
	 * @return The images identifier
	 */
	public static int getDrawableIdentifier(
			final Context context, final String filename) {
		return context.getResources().getIdentifier(filename, "drawable",
				context.getPackageName());
	}
}