package com.github.mrm1st3r.connection;

import android.bluetooth.BluetoothDevice;

/**
 * Event listener used to handle any connection state changes.
 * Empty default implementations allow omitting not needed methods.
 * 
 * @author Lukas Taake
 * @version 1.0
 */
public abstract class OnConnectionChangeHandler {

	/**
	 * A new connection was established.
	 * @param conn new established connection
	 */
	public void onConnect(final ThreadedConnection conn) {
		
	}

	/**
	 * A new connection failed to establish.
	 * @param dev remote device
	 */
	public void onConnectionFailed(final BluetoothDevice dev) {
		
	}
	/**
	 * A connection was disconnected.
	 * @param conn connection that was disconnected
	 */
	public void onDisconnect(final ThreadedConnection conn) {
		
	}
}