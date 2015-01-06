package com.github.mrm1st3r.connection;

import java.io.Closeable;

/**
 * 
 * @author Lukas 'mrm1st3r' Taake
 *
 * @param <T> Data type that will be sent and received
 */
public interface AsynchronousConnection<T> extends Closeable {

	/**
	 * Register callback handler that will be called when new data is received.
	 * @param receivedHandler Callback handler that will handle incoming data
	 */
	void setOnReceivedHandler(OnReceivedHandler<T> receivedHandler);
	
	/**
	 * Write data to connection.
	 * @param data Data to be sent
	 */
	void write(T data);
}
