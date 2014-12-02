package com.github.mrm1st3r.cards.connection;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.github.mrm1st3r.cards.R;

public class ClientThread extends Thread {
	
	private static final String TAG = ClientThread.class.getSimpleName();

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a ListView
				// mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				BluetoothSocket tmp = null;
				try {
					tmp = device.createRfcommSocketToServiceRecord(
							UUID.fromString(
									Resources.getSystem().getString(
											R.string.bt_uuid)));
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Log.d(TAG, tmp.getRemoteDevice().getAddress());
			}
		}
	};


	public ClientThread() {

		// open bluetooth server-socket
		
	}

	@Override
	public void run() {

	}
}
