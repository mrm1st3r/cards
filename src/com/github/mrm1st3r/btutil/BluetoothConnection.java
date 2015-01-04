package com.github.mrm1st3r.btutil;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public abstract class BluetoothConnection extends Thread {

	private static final String TAG = BluetoothConnection.class.getSimpleName();
	
	protected BluetoothSocket connection;
	protected boolean closing = false;
	
	public void close() {
		try {
			closing = true;
			connection.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	
}
