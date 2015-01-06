package com.github.mrm1st3r.connection;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * @author Lukas Taake
 * @version 1.0.0
 */
public abstract class BluetoothConnection extends Thread {

	/**
	 * Debug tag.
	 */
	private static final String TAG = BluetoothConnection.class.getSimpleName();
	/**
	 * Raw connection.
	 */
	private BluetoothSocket connection;
	/**
	 * Closing flag.
	 */
	private boolean closing = false;

	/**
	 * @return true if connection currently closing, otherwise false
	 */
	public final boolean isClosing() {
		return closing;
	}
	/**
	 * @return the used bluetooth socket
	 */
	public final BluetoothSocket getSocket() {
		return connection;
	}
	/**
	 * Set a new bluetooth socket.
	 * @param sock new socket
	 */
	protected final void setSocket(final BluetoothSocket sock) {
		connection = sock;
	}
	/**
	 * Close this connection.
	 */
	public final void close() {
		try {
			closing = true;
			connection.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}	
}
