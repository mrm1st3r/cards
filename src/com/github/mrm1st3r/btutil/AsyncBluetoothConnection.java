package com.github.mrm1st3r.btutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class AsyncBluetoothConnection extends BluetoothConnection {
	
	private static final String TAG = AsyncBluetoothConnection.class.getSimpleName();
	
	private final BufferedReader in;
	private final PrintWriter out;
	
	private OnMessageReceivedHandler handler;
	private OnDisconnectHandler dcHandler;
	private AtomicBoolean pausing = new AtomicBoolean(false);

	public AsyncBluetoothConnection(final BluetoothSocket sock, OnMessageReceivedHandler inHandler) {
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		
		try {
			tmpIn = sock.getInputStream();
			tmpOut = sock.getOutputStream();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		
		in = new BufferedReader(new InputStreamReader(tmpIn));
		out = new PrintWriter(tmpOut);
		connection = sock;
		handler = inHandler;
	}

	public void setReceiveHandler(OnMessageReceivedHandler newHand) {
		handler = newHand;
	}

	public void setOnDisconnectHandler(OnDisconnectHandler newHand) {
		dcHandler = newHand;
	}

	@Override
	public void run() {
		String str;
		while (true) {
			synchronized (pausing) {
				if (pausing.get() == true) {
					try {
						pausing.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				str = in.readLine();
				Log.d(TAG, "incoming: " + str);
				if (str == null) {
					continue;
				}
				if (handler != null) {
					handler.onMessageReceived(str);
				}
			} catch (IOException e) {
				if (!closing) {
					Log.w(TAG, e);
				}
				if (dcHandler != null) {
					dcHandler.onDisconnect(this);
				}
				break;
			}
		}
	}

	public void pause() {
		synchronized (pausing) {
			pausing.set(true);
		}
	}

	public void unpause() {
		synchronized (pausing) {
			pausing.set(false);
			pausing.notify();
		}
	}

	public void write(String str) {
		out.println(str);
		out.flush();
	}
}
