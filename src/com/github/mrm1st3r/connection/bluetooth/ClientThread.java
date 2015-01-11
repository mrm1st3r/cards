package com.github.mrm1st3r.connection.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.mrm1st3r.connection.OnConnectionChangeHandler;

/**
 * Helper thread to establish a bluetooth connection as a client.
 * 
 * Using a separate thread to establish a new bluetooth connection
 * is useful, because {@link BluetoothSocket#connect()} will block
 * as long as it takes to finish connecting (what might include
 * a confirmative user input).
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.1
 */
public class ClientThread extends ConnectThread {

	/**
	 * Debug tag.
	 */
	private static final String TAG = ClientThread.class.getSimpleName();
	/**
	 * Closing flag.
	 */
	private boolean closing = false;
	/**
	 * Socket that is opened while connecting.
	 */
	private final BluetoothSocket mmSocket;
	/**
	 * Remote device that should be connected to.
	 */
	private final BluetoothDevice mmDevice;

	/**
	 * Create a new client thread for a given remote device and service UUID.
	 * @param dev Remote device to connect to
	 * @param uuid Service UUID that is also used by server application
	 */
	public ClientThread(final BluetoothDevice dev, final UUID uuid) {

		BluetoothSocket tmp = null;

		try {
			tmp = dev.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) {
			Log.w(TAG, e);
		}

		mmSocket = tmp;
		mmDevice = dev;
	}

	/**
	 * Create a new client thread for a given remote device and service UUID
	 * and register a callback handler.
	 * @param dev Remote device to connect to
	 * @param uuid Service UUID that is also used by server application
	 * @param connHandler callback handler to be registered
	 */
	public ClientThread(final BluetoothDevice dev, final UUID uuid,
			final OnConnectionChangeHandler connHandler) {
		this(dev, uuid);
		setOnConnectionChangeHandler(connHandler);
	}

	@Override
	public final void run() {

		// Cancel discovery as is would slow down the connection
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

		try {
			// Try to connect to the remote device
			mmSocket.connect();
		} catch (Exception connectException) {

			// Don't react if the connection was meant to be closed
			if (closing) {
				return;
			}

			Log.w(TAG, connectException);

			if (getHandler() != null) {
				getHandler().onConnectionFailed(mmDevice);
			}

			try {
				mmSocket.close();
			} catch (IOException closeException) {
				Log.d(TAG, "Exception during closing...");
			}
			return;
		}
		// Create a new asynchronous connection object for the
		// established connection
		SimpleBluetoothConnection conn =
				new SimpleBluetoothConnection(mmSocket, null);

		if (getHandler() != null) {
			getHandler().onConnect(conn);
		}
	}

	@Override
	public final void close() {
		try {
			closing = true;
			mmSocket.close();
		} catch (IOException e) {
			Log.d(TAG, e.toString());
		}
	}

}