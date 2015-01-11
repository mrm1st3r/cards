package com.github.mrm1st3r.connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

/**
 * Base class for asynchronous and other connections that need to run
 * an endless loop in a separate thread.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public abstract class ThreadedConnection extends Thread implements Closeable {

	/**
	 * Debug tag.
	 */
	private static final String TAG = ThreadedConnection.class.getSimpleName();
	/**
	 * Handler that is called, when the connection get's lost.
	 */
	private OnConnectionChangeHandler connectionHandler;
	/**
	 * Indicator for pausing the connection.
	 */
	private AtomicBoolean pausing = new AtomicBoolean(false);

	/**
	 * Register a handler that will react to a disconnect.
	 * @param newHand handler to be called
	 */
	public final void setOnConnectionChangeHandler(
			final OnConnectionChangeHandler newHand) {
		connectionHandler = newHand;
	}

	/**
	 * Action that is performed on each loop turn.
	 * @throws IOException on error
	 */
	protected abstract void onRun() throws IOException;
	/**
	 * Action that is performed after the connection was paused.
	 */
	protected abstract void onResume();
	
	@Override
	public final void run() {
		while (true) {
			synchronized (pausing) {
				if (pausing.get()) {
					try {
						pausing.wait();
						onResume();
					} catch (InterruptedException e) {
						Log.d(TAG, "Interrupted during pause");
						// try to get paused again
						continue;
					}
				}
			}

			try {
				onRun();
			} catch (IOException e) {
				if (pausing.get()) {
					Log.w(TAG, "connection error while paused");
					Log.w(TAG, e);
					continue;
				}

				try {
					close();
				} catch (IOException e1) {
					Log.w(TAG, e1);
				}

				if (connectionHandler != null) {
					connectionHandler.onDisconnect(this);
				}
				break;
			}
		}
	}

	/**
	 * Pause this connections background thread.
	 */
	public final void pause() {
		synchronized (pausing) {
			pausing.set(true);
		}
	}

	/**
	 * Unpause this connections background thread.
	 */
	public final void unpause() {
		synchronized (pausing) {
			pausing.set(false);
			pausing.notify();
		}
	}

	/**
	 * @return current pausing status
	 */
	public final boolean isPaused() {
		return pausing.get();
	}
}