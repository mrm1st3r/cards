package com.github.mrm1st3r.btutil;

import android.bluetooth.BluetoothDevice;

public interface OnConnectHandler {

	public void onConnect(BluetoothConnection conn);

	public void onConnectionFailed(BluetoothDevice dev);
}
