package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * This class describes a remote player connected via Bluetooth.
 * @author Lukas 'mrm1st3r' Taake
 *
 */
public class BluetoothPlayer extends Player {
	
	/**
	 * The Bluetooth connection to the remote device.
	 */
	private SimpleBluetoothConnection connection;
	/**
	 * Construct a new remote player.
	 * @param pName The player name
	 * @param pHandSize The maximum number of hand cards
	 * @param pLifes The number of lifes to begin with
	 * @param pConn The remote connection
	 */
	public BluetoothPlayer(final String pName, final int pHandSize,
			final int pLifes, final SimpleBluetoothConnection pConn) {

		super(pName, pHandSize, pLifes);
		connection = pConn;
	}

	@Override
	public final void sendMessage(final String msg) {
		connection.write(msg);
	}

	/**
	 * @return The used Bluetooth connection to the remote device
	 */
	public final SimpleBluetoothConnection getConn() {
		return connection;
	}
}
