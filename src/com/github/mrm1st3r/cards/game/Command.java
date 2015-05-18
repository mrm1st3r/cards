package com.github.mrm1st3r.cards.game;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.util.Log;

/**
 * This class describes a command that is sent between the game and the players.
 * @author Lukas 'mrm1st3r' Taake
 *
 */
public class Command {

	/**
	 * Debug Tag.
	 */
	private static final String TAG = Command.class.getSimpleName();

	/**
	 * Command name.
	 */
	private String mCommand;
	/**
	 * Arguments for the command.
	 */
	private String[] mArgs;
	
	/**
	 * Construct a new command object from an encoded string.
	 * @param encoded Encoded command string
	 */
	public Command(final String encoded) {
		String[] split = encoded.split(" ");
		
		mCommand = split[0];
		mArgs = new String[split.length - 1];
		
		for (int i = 1; i < split.length; i++) {
			try {
				mArgs[i - 1] = URLDecoder.decode(split[i], "utf8");
			} catch (UnsupportedEncodingException e) {
				Log.w(TAG, e);
			}
		}
	}

	/**
	 * Get the command.
	 * @return The command
	 */
	public final String getCommand() {
		return mCommand;
	}

	/**
	 * Get an argument.
	 * @param pos Number of the argument
	 * @return An argument
	 */
	public final String getArg(final int pos) {
		return mArgs[pos];
	}
	/**
	 * Get all arguments.
	 * @return All arguments
	 */
	public final String[] getArgs() {
		return mArgs;
	}
	
	/**
	 * Encode a command to send.
	 * @param pComm Command to send
	 * @param pParams Parameters for command
	 * @return Encoded command
	 */
	public static String encode(
			final String pComm,
			final String... pParams) {

		StringBuilder build = new StringBuilder(pComm);

		for (String s : pParams) {
			build.append(' ');

			try {
				build.append(URLEncoder.encode(s, "utf8"));
			} catch (UnsupportedEncodingException e) {
				Log.w(TAG, e);
			}
		}

		return build.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof String) {
			return mCommand.equals(o);
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return mCommand.hashCode();
	}
}
