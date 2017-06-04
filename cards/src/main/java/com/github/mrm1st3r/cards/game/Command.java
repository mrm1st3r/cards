package com.github.mrm1st3r.cards.game;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.util.Log;

/**
 * This class describes a command that is sent between the game and the players.
 */
public class Command {

	private static final String TAG = Command.class.getSimpleName();

	private final String mCommand;
	private final String[] mArgs;

	public Command(String encoded) {
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

	public String getCommand() {
		return mCommand;
	}

	public String getArg(int pos) {
		return mArgs[pos];
	}

	public String[] getArgs() {
		return mArgs;
	}

	static String encode(String pComm, String... pParams) {
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
	public int hashCode() {
		return mCommand.hashCode();
	}
}
