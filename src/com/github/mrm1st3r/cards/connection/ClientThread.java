package com.github.mrm1st3r.cards.connection;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.BluetoothConnection;
import com.github.mrm1st3r.btutil.OnConnectHandler;
import com.github.mrm1st3r.cards.R;


public class ClientThread extends Thread {

	private static final String TAG = ClientThread.class.getSimpleName();
	private boolean closing = false;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;

	private OnConnectHandler handler = null;

	public ClientThread(Context context, BluetoothDevice dev,
			OnConnectHandler connHandler) {

		handler = connHandler;
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = dev;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(
					context.getString(
							R.string.bt_uuid)));
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		mmSocket = tmp;

	}

	@Override
	public void run() {
		// Cancel discovery because it will slow down the connection
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException connectException) {
			if (closing) {
				return;
			}
			Log.w(TAG, connectException);
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException closeException) { }
			return;
		}

		// Do work to manage the connection (in a separate thread)
		BluetoothConnection conn = new AsyncBluetoothConnection(mmSocket, null);
		conn.start();
		handler.onConnect(conn);
	}

	public void cancel() {
		try {
			closing = true;
			mmSocket.close();
		} catch (IOException e) { }
	}

}
