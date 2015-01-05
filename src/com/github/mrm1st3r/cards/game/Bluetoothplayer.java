package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.OnMessageReceivedHandler;

public class Bluetoothplayer extends Player{
	
	/**
	 * Connection des Spielers
	 */
	AsyncBluetoothConnection conn;

	public Bluetoothplayer(String n, int m, AsyncBluetoothConnection conn) {
		super(n, m);
		setConn(conn);
		
		conn.setReceiveHandler(new OnMessageReceivedHandler() {
			@Override
			public void onMessageReceived(String msg) {
				
				// tu was
				synchronized (game.playerLock) {
					game.playerLock.notify();
				}
			}
		});
	}

	@Override
	public void connect(String msg){
		conn.write(msg);
	}

	/**
	 * Getter f�r {@link #conn}
	 * 
	 * @return {@link #conn}
	 */
	public AsyncBluetoothConnection getConn() {
		return conn;
	}

	/**
	 * Setter f�r {@link #conn}
	 * 
	 * @param conn um {@link #conn} zu definieren
	 */
	public void setConn(AsyncBluetoothConnection conn) {
		this.conn = conn;
	}
	
}
