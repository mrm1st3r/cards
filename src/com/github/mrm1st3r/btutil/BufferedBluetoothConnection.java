package com.github.mrm1st3r.btutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BufferedBluetoothConnection extends BluetoothConnection {

	private static final long serialVersionUID = 1L;
	private static final String TAG = BufferedBluetoothConnection.class.getSimpleName();
	private static final int READ_BUFFER_SIZE = 4096;

	private final InputStream in;
	private final OutputStream out;
	private final Object readLock = new Object();
	private final Object writeLock = new Object();
	private final byte[] readBuffer = new byte[READ_BUFFER_SIZE];
	private int bufferPos = 0;
	private int bufferLength = 0;
	private boolean closing = false;


	public BufferedBluetoothConnection(final BluetoothSocket sock) {
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		try {
			tmpIn = sock.getInputStream();
			tmpOut = sock.getOutputStream();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
		in = tmpIn;
		out = tmpOut;
		connection = sock;
	}

	public void write(byte[] data) throws IOException {
		synchronized (writeLock) {
			out.write(data);
		}
	}

	public void read(byte[] data, int timeoutMillis) throws IOException {
		int bytesMissing = 0;
		long timeStart = System.currentTimeMillis();
		// wait until enough bytes are received
		do {
			synchronized (readLock) {
				bytesMissing = data.length - bufferLength;
			}
			//Log.d(TAG, "bytes missing: " + bytesMissing);
			//Log.d(TAG, "millis left: " + ((timeStart + timeoutMillis) - System.currentTimeMillis()));
		} while (bytesMissing > 0 && 
				((timeStart + timeoutMillis) > System.currentTimeMillis()));
		
		if (bytesMissing > 0) {
			throw new IOException("No bytes to read");
		}
		
		// copy data into given array
		synchronized (readLock) {
			if (bufferPos + data.length < readBuffer.length) {
				System.arraycopy(readBuffer, bufferPos, data, 0, data.length);
			} else {
				int bytesLeft = readBuffer.length - bufferPos;
				System.arraycopy(readBuffer, bufferPos, data, 0, bytesLeft);
				System.arraycopy(readBuffer, 0, data, bytesLeft,
						(data.length - bytesLeft));
			}
			bufferPos += data.length;
			bufferLength -= data.length;
		}
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = in.read(buffer);
				if (bytes == 0) {
					continue;
				}
				//Log.d(TAG, HexDump.toHexString(buffer));
				synchronized (readLock) {
					bufferLength += bytes;
					// copy read bytes into ring buffer
					if (bufferPos + bytes < readBuffer.length) {
						System.arraycopy(buffer, 0, readBuffer, bufferPos, bytes);
					} else {
						// split up received data into two pieces to fit in buffer
						int bytesLeft = readBuffer.length - bufferPos;
						System.arraycopy(buffer, 0, readBuffer, bufferPos, bytesLeft);
						System.arraycopy(buffer, bytesLeft, readBuffer, 0, (bytes - bytesLeft));
					}
				}
				// Send the obtained bytes to the UI activity
				//  mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
				//         .sendToTarget();
			} catch (IOException e) {
				if (!closing) {
					Log.w(TAG, e);
				}
				break;
			}
		}
	}


}
