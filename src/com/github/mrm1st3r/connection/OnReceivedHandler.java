package com.github.mrm1st3r.connection;

/**
 * Handler for incoming data received by an {@link AsynchronousConnection}.
 * 
 * @author Lukas 'mrm1st3r' Taake
 *
 * @param <T>
 *            Message type that will be received
 */
public interface OnReceivedHandler<T> {

	/**
	 * Handle incoming data.
	 * 
	 * @param conn
	 *            Connection that received the data
	 * @param data
	 *            Received data
	 */
	void onReceived(AsynchronousConnection<T> conn, T data);
}
