package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

/**
 * This class describes a remote player connected via Bluetooth.
 * 
 * @author Lukas 'mrm1st3r' Taake, Sergius Maier
 * @version 1.0.2
 */
public class BluetoothPlayer extends Player {

	/**
	 * The Bluetooth connection to the remote device.
	 */
	private final SimpleBluetoothConnection mConnection;

	/**
	 * Construct a new remote player.
	 * 
	 * @param pName
	 *            Player name
	 * @param pConn
	 *            Player connection
	 */
	public BluetoothPlayer(final String pName,
			final SimpleBluetoothConnection pConn) {
		super(pName);
		mConnection = pConn;
	}

	/**
	 * Construct a new remote player.
	 * 
	 * @param pName
	 *            Player name
	 * @param pHandSize
	 *            Maximum number of hand cards
	 * @param pLifes
	 *            Number of lifes to begin with
	 * @param pConn
	 *            Remote connection
	 */
	public BluetoothPlayer(final String pName, final int pHandSize,
			final int pLifes, final SimpleBluetoothConnection pConn) {
		super(pName, pHandSize, pLifes);
		mConnection = pConn;
	}

	/**
	 * Get the connection to the players remote device.
	 * 
	 * @return The used Bluetooth connection to the remote device
	 */
	public final SimpleBluetoothConnection getConnection() {
		return mConnection;
	}

	@Override
	public final void command(final String msg) {
		mConnection.write(msg);
	}
}
