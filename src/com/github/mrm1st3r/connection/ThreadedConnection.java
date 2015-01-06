package com.github.mrm1st3r.connection;

import java.io.Closeable;

/**
 * Base class for asynchronous and other connections that need to run
 * an endless loop in a separate thread.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public abstract class ThreadedConnection extends Thread implements Closeable {

	/**
	 * Action that is performed on each loop turn.
	 */
	protected abstract void onRun();

	/**
	 * Register a handler that will react to a disconnect.
	 * @param h handler to be called
	 */
	public abstract void setOnConnectionChangeHandler(
			OnConnectionChangeHandler h);
}
