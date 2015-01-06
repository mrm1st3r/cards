package com.github.mrm1st3r.connection.bluetooth;

/**
 * Base class for Bluetooth connecting threads.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public abstract class ConnectThread extends Thread {

	/**
	 * Stop trying to connect.
	 */
	public abstract void cancel();
}
