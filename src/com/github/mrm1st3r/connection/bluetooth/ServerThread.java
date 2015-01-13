package com.github.mrm1st3r.connection.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.mrm1st3r.connection.OnConnectionChangeHandler;

/**
 * Helper thread to create a {@link BluetoothServerSocket} and wait for incoming
 * client connections.
 * 
 * Using a separate thread to wait for new bluetooth connections is useful,
 * because {@link BluetoothServerSocket#accept()} will block until a new
 * connection is established.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.1
 */
public class ServerThread extends ConnectThread {

	/**
	 * Debug tag.
	 */
	private static final String TAG = ServerThread.class.getSimpleName();
	/**
	 * Socket to listen for incoming connections.
	 */
	private final BluetoothServerSocket mmServerSocket;
	/**
	 * Closing flag.
	 */
	private boolean closing = false;

	/**
	 * Create a new server thread and listen with a given service name and UUID
	 * and register a callback handler.
	 * 
	 * @param name
	 *            Service name
	 * @param uuid
	 *            Service UUID
	 * @param handler
	 *            callback handler to be registered
	 */
	public ServerThread(final String name, final UUID uuid,
			final OnConnectionChangeHandler handler) {

		this(name, uuid);
		setOnConnectionChangeHandler(handler);
	}

	/**
	 * Create a new server thread and listen with a given service name and UUID.
	 * 
	 * @param name
	 *            Service name
	 * @param uuid
	 *            Service UUID
	 */
	public ServerThread(final String name, final UUID uuid) {

		BluetoothServerSocket tmp = null;
		try {
			// open bluetooth server-socket
			tmp = BluetoothAdapter.getDefaultAdapter()
					.listenUsingRfcommWithServiceRecord(name, uuid);
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		mmServerSocket = tmp;
	}

	@Override
	public final void run() {

		Log.d(TAG, "start listening for incoming connections");

		while (true) {
			BluetoothSocket socket = null;

			try {
				socket = mmServerSocket.accept();
				SimpleBluetoothConnection conn = new SimpleBluetoothConnection(
						socket, null);

				Log.d(TAG, "incoming connection from "
						+ socket.getRemoteDevice().getName());

				if (getHandler() != null) {
					getHandler().onConnect(conn);
				}
			} catch (Exception e) {
				// catch all exceptions to also get NullPointers if creating
				// mmServerSocket failed

				if (closing) {
					break;
				}
				close();
				Log.w(TAG, e);

				if (getHandler() != null) {
					getHandler().onConnectionFailed(null);
				}

				break;
			}
		}
	}

	@Override
	public final void close() {
		try {
			closing = true;
			mmServerSocket.close();
		} catch (Exception e) {
			Log.w(TAG, e);
		}
	}
}
