package com.github.mrm1st3r.libdroid.collections;

/**
 * This is a utility class for parsing byte arrays into other data types
 * and the other way round.
 * @author Lukas 'mrm1st3r' Taake <lukas.taake@gmail.com>
 * @version 1.0.1
 *
 */
public final class ByteArray {

	/**
	 * Number of bytes contained in an integer value.
	 */
	public static final int BYTES_PER_INT = Integer.SIZE / Byte.SIZE;
	
	/**
	 * Binary mask to match one byte.
	 */
	public static final int BYTE_MASK = 0xff;
	
	/**
	 * Decimal constant.
	 */
	public static final int DECIMAL = 10;

	/**
	 * Hidden constructor for utility class.
	 * @throws InstantiationException There shall be no object of this class
	 */
	private ByteArray() throws InstantiationException {
		throw new InstantiationException("There shall be no object");
	}

	/**
	 * Parse an integer from a given byte array.
	 * Will return 0, when a null object or array with length 0 is given.
	 * Any more bytes than fitting in an integer will be ignored.
	 * @param bytes Given bytes in big-endian order
	 * @return Parsed integer value
	 */
	public static int toInteger(final byte[] bytes) {
		return toInteger(bytes, 0, bytes.length);
	}

	/**
	 * Parse an integer from a given byte array.
	 * Will return 0, when a null object or array with length 0 is given.
	 * Any more bytes than fitting in an integer will be ignored.
	 * @param bytes Given bytes in big-endian order
	 * @param offset Array index to start with
	 * @param length Number of array elements to use
	 * @return Parsed integer value
	 */
	public static int toInteger(final byte[] bytes, final int offset,
			final int length) {

		if (length < 0 || offset < 0) {
			throw new IllegalArgumentException("cannot use negative indizes");
		}
		
		if (bytes == null || bytes.length == 0 || length == 0) {
			return 0;
		}
		if (bytes.length == 1) {
			return bytes[offset] & BYTE_MASK;
		}

		int ret = 0;

		for (int i = 0; i < length && i < BYTES_PER_INT; i++) {
			ret = ret << Byte.SIZE;
			ret += bytes[i + offset] & BYTE_MASK;
		}

		return ret;
	}

	/**
	 * Parse a double value from a given byte array.
	 * The bytes will be parsed as hard comma and being split at a specified
	 * position.
	 * 
	 * Will return 0, when a null object or array with length 0 is given.
	 * @param bytes Given bytes in big-endian order
	 * @param pos Number of bytes in front of the comma
	 * @return Parsed double value
	 */
	public static double toDouble(final byte[] bytes, final int pos) {
		
		if (bytes == null || bytes.length == 0) {
			return 0;
		}
		
		if (pos > bytes.length) {
			throw new IllegalArgumentException(
					"Split point is bigger than the given array");
		}
		
		double val, comma;
		
		val = toInteger(bytes, 0, pos);
		comma = toInteger(bytes, pos, (bytes.length - pos));
		
		while (comma >= 1) {
			comma /= DECIMAL;
		}
		val += comma;

		return val;
	}

	/**
	 * see {@link #fromInt(int, int, boolean)}.
	 * @param val number to split
	 * @param byteNum number of bytes to return
	 * @return big-endian ordered bytes
	 */
	public static byte[] fromInt(final int val, final int byteNum) {
		return fromInt(val, byteNum, true);
	}
	/**
	 * Split a integer into single bytes.
	 * @param val number to split
	 * @param byteNum number of bytes to return
	 * @param bigEndian use big-endian byte order
	 * @return ordered bytes
	 */
	public static byte[] fromInt(final int val, final int byteNum,
			final boolean bigEndian) {
		
		byte[] bytes = new byte[BYTES_PER_INT];

		for (int i = 0; i < Math.min(byteNum, BYTES_PER_INT); i++) {
			bytes[i] = (byte) ((val >> (Byte.SIZE * i)) & BYTE_MASK);
		}

		// change to big-endian and cut to required length
		byte[] ret = new byte[byteNum];
		for (int i = 0; i < byteNum; i++) {
			if (bigEndian) {
				ret[i] = bytes[byteNum - i - 1];
			} else {
				ret[i] = bytes[i];
			}
		}
		return ret;
	}
}
