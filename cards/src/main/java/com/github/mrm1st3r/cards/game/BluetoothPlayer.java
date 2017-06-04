package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

/**
 * This class describes a remote player connected via Bluetooth.
 */
public class BluetoothPlayer extends Player {

	private final SimpleBluetoothConnection connection;

	public BluetoothPlayer(String pName, SimpleBluetoothConnection pConn) {
		super(pName);
		connection = pConn;
	}

	public SimpleBluetoothConnection getConnection() {
		return connection;
	}

	@Override
	public void command(String msg) {
		connection.write(msg);
	}
}
