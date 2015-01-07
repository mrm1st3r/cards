package com.github.mrm1st3r.connection.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.ThreadedConnection;

/**
 * Implementation of {@link BluetoothConnection},
 * providing asynchronous connection for use with Strings.
 * Uses a {@link BufferedReader} for reading a {@link PrintWriter} for
 * writing.
 * 
 * @author Lukas Taake
 * @version 1.1.0
 */
public class SimpleBluetoothConnection
extends ThreadedConnection
implements AsynchronousConnection<String> {

	/**
	 * Tag for logging.
	 */
	private static final String TAG =
			SimpleBluetoothConnection.class.getSimpleName();
	/**
	 * Input connection.
	 */
	private final BufferedReader in;
	/**
	 * Output connection.
	 */
	private final PrintWriter out;

	/**
	 * Handler that is called, when a new message is received.
	 */
	private OnReceivedHandler<String> messageHandler;
	/**
	 * Buffer for data that is read while connection gets paused.
	 */
	private String buffer = null;
	/**
	 * Underlying Bluetooth socket.
	 */
	private final BluetoothSocket socket;

	/**
	 * Create a new connection without any handlers.
	 * @param sock Socket to use
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock) {

		BufferedReader tmpIn = null;
		PrintWriter tmpOut = null;

		try {
			tmpIn = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			tmpOut = new PrintWriter(sock.getOutputStream());
		} catch (IOException e) {
			Log.w(TAG, e);
		}

		in = tmpIn;
		out = tmpOut;
		socket = sock;
	}

	/**
	 * Create a new connection with only a message handler.
	 * @param sock Socket to use
	 * @param inHand message handler
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock,
			final OnReceivedHandler<String> inHand) {
		this(sock);
		setOnReceivedHandler(inHand);
	}

	/**
	 * Create a new connection with receive and connection handler.
	 * @param sock Socket to use
	 * @param inHand message handler
	 * @param dcHand disconnect handler
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock,
			final OnReceivedHandler<String> inHand,
			final OnConnectionChangeHandler dcHand) {
		this(sock);
		setOnReceivedHandler(inHand);
		setOnConnectionChangeHandler(dcHand);
	}

	@Override
	public final void setOnReceivedHandler(
			final OnReceivedHandler<String> newHand) {
		messageHandler = newHand;
	}



	@Override
	protected final void onRun() throws IOException {
		String str = in.readLine();
		Log.d(TAG, "incoming: " + str);
		if (str == null) {
			return;
		}
		if (isPaused()) {
			buffer = str;
			return;
		}
		if (messageHandler != null) {
			messageHandler.onReceived(this, str);
		}
	}
	@Override
	protected final void onResume() {
		if (buffer != null && messageHandler != null) {
			messageHandler.onReceived(this, buffer);
			buffer = null;
		}
	}

	/**
	 * @return The remote devices name
	 */
	public final String getDeviceName() {
		return socket.getRemoteDevice().getName();
	}

	@Override
	public final void write(final String str) {
		out.println(str);
		out.flush();
	}

	@Override
	public final void close() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}	
}
