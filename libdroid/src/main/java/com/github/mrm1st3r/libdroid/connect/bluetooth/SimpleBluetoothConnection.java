package com.github.mrm1st3r.libdroid.connect.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;

/**
 * Implementation of {@link AsynchronousConnection} for use with Strings.
 * Uses a {@link BufferedReader} for reading and a {@link PrintWriter}
 * for writing.
 * 
 * @author Lukas Taake
 * @version 1.1
 */
public class SimpleBluetoothConnection extends ThreadedConnection implements
		AsynchronousConnection<String> {

	/**
	 * Tag for logging.
	 */
	private static final String TAG = SimpleBluetoothConnection.class
			.getSimpleName();
	/**
	 * Input connection.
	 */
	private final BufferedReader mIn;
	/**
	 * Output connection.
	 */
	private final PrintWriter mOut;

	/**
	 * Handler that is called, when a new message is received.
	 */
	private OnReceivedHandler<String> messageHandler;
	/**
	 * Buffer for data that is read while connection gets paused.
	 */
	private String mBuffer = null;
	/**
	 * Underlying Bluetooth socket.
	 */
	private final BluetoothSocket mSocket;

	/**
	 * Create a new connection without any handlers.
	 * 
	 * @param sock
	 *            Socket to use
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock) {

		BufferedReader tmpIn = null;
		PrintWriter tmpOut = null;

		try {
			tmpIn = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			tmpOut = new PrintWriter(sock.getOutputStream());
		} catch (IOException e) {
			Log.w(TAG, e);
		}

		mIn = tmpIn;
		mOut = tmpOut;
		mSocket = sock;

		// Set thread name for debugging
		this.setName("Connection to " + mSocket.getRemoteDevice().getName());
	}

	/**
	 * Create a new connection with only a message handler.
	 * 
	 * @param sock
	 *            Socket to use
	 * @param inHand
	 *            message handler
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock,
			final OnReceivedHandler<String> inHand) {
		this(sock);
		setOnReceivedHandler(inHand);
	}

	/**
	 * Create a new connection with receive and connection handler.
	 * 
	 * @param sock
	 *            Socket to use
	 * @param inHand
	 *            message handler
	 * @param dcHand
	 *            disconnect handler
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
		String str = mIn.readLine();
		Log.v(TAG, "incoming: " + str);
		if (str == null) {
			return;
		}
		if (isPaused()) {
			mBuffer = str;
			return;
		}
		if (messageHandler != null) {
			messageHandler.onReceived(this, str);
		}
	}

	@Override
	protected final void onResume() {
		if (mBuffer != null && messageHandler != null) {
			messageHandler.onReceived(this, mBuffer);
			mBuffer = null;
		}
	}

	/**
	 * @return The remote devices name
	 */
	public final String getDeviceName() {
		return mSocket.getRemoteDevice().getName();
	}

	@Override
	public final void write(final String str) {
		Log.v(TAG, "writing: " + str);
		mOut.println(str);
		mOut.flush();
	}

	@Override
	public final void close() {
		try {
			mSocket.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}
}
