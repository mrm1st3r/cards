package com.github.mrm1st3r.connection.bluetooth;

import java.io.Closeable;

import com.github.mrm1st3r.connection.OnConnectionChangeHandler;

/**
 * Base class for Bluetooth connecting threads.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public abstract class ConnectThread extends Thread implements Closeable {

	/**
	 * Handler that will react on a succeeded or failed connection attempt.
	 */
	private OnConnectionChangeHandler handler = null;

	/**
	 * Register a handler that will react to a
	 * failed or succeeded connection attempt.
	 * @param h Handler to be registered
	 */
	public final void setOnConnectionChangeHandler(
			final OnConnectionChangeHandler h) {
		handler = h;
	}

	/**
	 * @return The currently registered handler
	 */
	protected final OnConnectionChangeHandler getHandler() {
		return handler;
	}
}
