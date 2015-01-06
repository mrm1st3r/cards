package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

public class Bluetoothplayer extends Player{
	
	/**
	 * Connection des Spielers
	 */
	SimpleBluetoothConnection connection;

	public Bluetoothplayer(String n, int m, SimpleBluetoothConnection conn) {
		super(n, m);
		setConn(conn);
		
		conn.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {
				
				// tu was
				synchronized (game.playerLock) {
					game.playerLock.notify();
				}
			}
		});
	}

	@Override
	public void connect(String msg){
		connection.write(msg);
	}

	/**
	 * Getter f�r {@link #connection}
	 * 
	 * @return {@link #connection}
	 */
	public SimpleBluetoothConnection getConn() {
		return connection;
	}

	/**
	 * Setter f�r {@link #connection}
	 * 
	 * @param conn um {@link #connection} zu definieren
	 */
	public void setConn(SimpleBluetoothConnection conn) {
		this.connection = conn;
	}
	
}
