package com.github.mrm1st3r.cards;

import java.util.HashMap;

import android.app.Application;

import com.github.mrm1st3r.connection.bluetooth.BluetoothUtil;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * Application class that is used to hold bluetooth connections while activities
 * are changed.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public class Cards extends Application {

	/**
	 * Preference file.
	 */
	public static final String PREF_FILE =
			"com.github.mrm1st3r.cards.preferences";
	/**
	 * Preference name for the player name.
	 */
	public static final String PREF_PLAYER_NAME = "PREF_PLAYER_NAME";
	/**
	 * Service UUID for bluetooth connection.
	 */
	public static final String UUID = "1dbb8dc0-6f38-11e4-9803-0800200c9a66";
	/**
	 * Contains all currently active bluetooth connections.
	 */
	private HashMap<SimpleBluetoothConnection, String> connections =
			new HashMap<SimpleBluetoothConnection, String>();
	/**
	 * True if bluetooth was enabled by the application.
	 */
	private boolean enabledBluetooth = false;

	/**
	 * @return List of all established connections.
	 */
	public final HashMap<SimpleBluetoothConnection, String> getConnections() {
		return connections;
	}

	/**
	 * Replace the current list of connections with a new one.
	 * 
	 * @param conn
	 *            New list of connections
	 */
	public final void setConnections(
			final HashMap<SimpleBluetoothConnection, String> conn) {
		connections = conn;
	}
	/**
	 * Register if bluetooth was enabled by the application.
	 * @param state true if bluetooth was enabled by the application
	 */
	public final void setEnabled(final boolean state) {
		enabledBluetooth = state;
	}
	/**
	 * Reset the local bluetooth adapter to the state it was when the
	 * application was launched.
	 */
	public final void resetBluetoothAdapter() {
		if (enabledBluetooth) {
			BluetoothUtil.disable();
		}
	}
}
