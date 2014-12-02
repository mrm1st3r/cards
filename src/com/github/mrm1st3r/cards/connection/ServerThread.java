package com.github.mrm1st3r.cards.connection;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.util.Log;

import com.github.mrm1st3r.cards.R;

public class ServerThread extends Thread {
	
	private static final String TAG = ServerThread.class.getSimpleName();

	private final BluetoothServerSocket mmServerSocket;

	public ServerThread() {
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;
		try {
			// open bluetooth server-socket
			tmp = BluetoothAdapter.getDefaultAdapter().
					listenUsingRfcommWithServiceRecord(
							Resources.getSystem().getString(R.string.app_name),
							UUID.fromString(
									Resources.getSystem().getString(
											R.string.bt_uuid)));
		} catch (IOException e) { }
		mmServerSocket = tmp;
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection (in a separate thread)
			
				manageConnectedSocket(socket);
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket sock) {
		Log.d(TAG, sock.getRemoteDevice().getAddress());
		try {
			sock.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) { }
	}

}
