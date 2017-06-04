package com.github.mrm1st3r.cards;

import java.util.HashMap;

import android.app.Application;

import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

/**
 * Application class that is used to hold bluetooth connections while activities are changed.
 */
public class Cards extends Application {

	public static final String PREF_FILE = "com.github.mrm1st3r.cards.preferences";
	public static final String PREF_PLAYER_NAME = "PREF_PLAYER_NAME";
	public static final String UUID = "1dbb8dc0-6f38-11e4-9803-0800200c9a66";
	/**
	 * Extra field for intents that contains the local player name.
	 */
	public static final String EXTRA_LOCAL_NAME = "EXTRA_LOCAL_NAME";

	private HashMap<SimpleBluetoothConnection, String> mConnections = new HashMap<>();

	public HashMap<SimpleBluetoothConnection, String> getConnections() {
		return mConnections;
	}

	public void setConnections(HashMap<SimpleBluetoothConnection, String> conn) {
		mConnections = conn;
	}
}
