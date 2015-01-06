package com.github.mrm1st3r.connection.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.BluetoothConnection;
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
public class SimpleBluetoothConnection extends ThreadedConnection implements AsynchronousConnection<String> {

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
	 * Handler that is called, when the connection get's lost.
	 */
	private OnConnectionChangeHandler connectionHandler;
	/**
	 * Indicator for pausing the connection.
	 */
	private AtomicBoolean pausing = new AtomicBoolean(false);
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
	 * @param sock raw connection to use
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock) {
		this(sock, null, null);
	}

	/**
	 * Create a new connection with only a message handler.
	 * @param sock raw connection to use.
	 * @param inHand message handler
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock,
			final OnReceivedHandler<String> inHand) {
		this(sock, inHand, null);
	}

	/**
	 * Create a new connection.
	 * @param sock underlying socket connection
	 * @param inHand message handler
	 * @param dcHand disconnect handler
	 */
	public SimpleBluetoothConnection(final BluetoothSocket sock,
			final OnReceivedHandler<String> inHand,
			final OnConnectionChangeHandler dcHand) {
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
		messageHandler = inHand;
		connectionHandler = dcHand;
	}

	/**
	 * 
	 * @param newHand
	 */
	@Override
	public final void setOnReceivedHandler(
			final OnReceivedHandler<String> newHand) {
		messageHandler = newHand;
	}

	@Override
	public final void setOnConnectionChangeHandler(
			final OnConnectionChangeHandler newHand) {
		connectionHandler = newHand;
	}
	
	@Override
	protected final void onRun() {
		
	}

	@Override
	public final void run() {
		String str;
		while (true) {
			synchronized (pausing) {
				if (pausing.get()) {
					try {
						pausing.wait();
						// received message while paused
						if (buffer != null && messageHandler != null) {
							messageHandler.onReceived(this, buffer);
							buffer = null;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				str = in.readLine();
				Log.d(TAG, "incoming: " + str);
				if (str == null) {
					continue;
				}
				if (pausing.get()) {
					buffer = str;
					continue;
				}
				if (messageHandler != null) {
					messageHandler.onReceived(this, str);
				}
			} catch (IOException e) {
				if (pausing.get()) {
					Log.w(TAG, "connection error while paused");
					// error might 
					continue;
				}

				close();

				if (connectionHandler != null) {
					connectionHandler.onDisconnect(this);
				}
				break;
			}
		}
	}

	public void pause() {
		synchronized (pausing) {
			pausing.set(true);
		}
	}

	public void unpause() {
		synchronized (pausing) {
			pausing.set(false);
			pausing.notify();
		}
	}

	public String getDeviceName() {
		return socket.getRemoteDevice().getName();
	}

	public void write(String str) {
		out.println(str);
		out.flush();
	}
	
	public void close() {
		
	}
}
