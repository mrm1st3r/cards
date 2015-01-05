package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;

public class Bluetoothplayer extends Player{
	
	/**
	 * Connection des Spielers
	 */
	AsyncBluetoothConnection conn;

	public Bluetoothplayer(String n, int m, AsyncBluetoothConnection conn) {
		super(n, m);
		setConn(conn);
	}

	@Override
	public void connect(String msg){
		conn.write(msg);
	}

	/**
	 * Getter für {@link #conn}
	 * 
	 * @return {@link #conn}
	 */
	public AsyncBluetoothConnection getConn() {
		return conn;
	}

	/**
	 * Setter für {@link #conn}
	 * 
	 * @param conn um {@link #conn} zu definieren
	 */
	public void setConn(AsyncBluetoothConnection conn) {
		this.conn = conn;
	}
	
}
