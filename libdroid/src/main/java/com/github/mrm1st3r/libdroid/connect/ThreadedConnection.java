package com.github.mrm1st3r.libdroid.connect;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

/**
 * Base class for asynchronous and other connections that need to run an endless
 * loop in a separate thread.
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
	private OnConnectionChangeHandler mConnectionHandler;
	/**
	 * Indicator for pausing the connection.
	 */
	private AtomicBoolean mPausing = new AtomicBoolean(false);
	/**
	 * Lock for accessing pause status.
	 */
	private Object mPausingLock = new Object();

	/**
	 * Register a handler that will react to a disconnect.
	 * 
	 * @param newHand
	 *            handler to be called
	 */
	public final void setOnConnectionChangeHandler(
			final OnConnectionChangeHandler newHand) {
		mConnectionHandler = newHand;
	}

	/**
	 * Action that is performed on each loop turn.
	 * 
	 * @throws IOException
	 *             on error
	 */
	protected abstract void onRun() throws IOException;

	/**
	 * Action that is performed after the connection was paused.
	 */
	protected abstract void onResume();

	@Override
	public final void run() {
		while (true) {
			synchronized (mPausingLock) {
				if (mPausing.get()) {
					try {
						mPausingLock.wait();
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
				if (mPausing.get()) {
					Log.w(TAG, "connection error while paused");
					Log.w(TAG, e);
					continue;
				}

				try {
					close();
				} catch (IOException e1) {
					Log.w(TAG, e1);
				}

				if (mConnectionHandler != null) {
					mConnectionHandler.onDisconnect(this);
				}
				break;
			}
		}
	}

	/**
	 * Pause this connections background thread.
	 */
	public final void pause() {
		synchronized (mPausingLock) {
			mPausing.set(true);
		}
	}

	/**
	 * Unpause this connections background thread.
	 */
	public final void unpause() {
		synchronized (mPausingLock) {
			mPausing.set(false);
			mPausingLock.notify();
		}
	}

	/**
	 * @return current pausing status
	 */
	public final boolean isPaused() {
		return mPausing.get();
	}
}
