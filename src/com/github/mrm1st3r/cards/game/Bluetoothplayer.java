package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

public class Bluetoothplayer extends Player{
	
	/**
	 * Connection des Spielers.
	 */
	private SimpleBluetoothConnection connection;
	
	private Object lock = new Object();

	public Bluetoothplayer(String n, int m, int l, SimpleBluetoothConnection conn) {
		super(n, m, l);
		setConn(conn);
		
		conn.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {
				synchronized (lock) {
					// game.checkMessage(msg);
					lock.notify();
				}
			}
		});
	}

	@Override
	public void sendMessage(String msg){
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
