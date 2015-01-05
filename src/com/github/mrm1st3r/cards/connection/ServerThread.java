package com.github.mrm1st3r.cards.connection;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.BluetoothConnection;
import com.github.mrm1st3r.btutil.OnConnectHandler;
import com.github.mrm1st3r.cards.R;

public class ServerThread extends Thread {
	
	private static final String TAG = ServerThread.class.getSimpleName();

	private final BluetoothServerSocket mmServerSocket;
	private Context context = null;
	private boolean closing = false;
	private OnConnectHandler handler = null;

	public ServerThread(final Context con, OnConnectHandler connHandler) {
		context = con;
		handler = connHandler;
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;
		try {
			// open bluetooth server-socket
			tmp = BluetoothAdapter.getDefaultAdapter().
					listenUsingRfcommWithServiceRecord(
							context.getString(R.string.app_name),
							UUID.fromString(
									context.getString(
											R.string.bt_uuid)));
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		mmServerSocket = tmp;
	}

	@Override
	public void run() {
		if (mmServerSocket == null) {
			return;
		}
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned
		while (true) {
			try {
				Log.d(TAG, "waiting for connection");
				socket = mmServerSocket.accept();
				Log.d(TAG, "Incoming connection... " + socket);
			} catch (IOException e) {
				if (closing) {
					return;
				}
				Log.w(TAG, e);
				break;
			}
			// If a connection was accepted
			if (socket != null) {
				manageConnectedSocket(socket);
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket sock) {
		Log.d(TAG, sock.getRemoteDevice().getAddress());
		/*try {
			//PrintWriter out = new PrintWriter(sock.getOutputStream());
			//out.println("test 123");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}*/
		BluetoothConnection conn = new AsyncBluetoothConnection(sock, null);
		conn.start();
		handler.onConnect(conn);
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			closing = true;
			mmServerSocket.close();
		} catch (Exception e) {
			Log.w(TAG, e);
		}
	}

}
