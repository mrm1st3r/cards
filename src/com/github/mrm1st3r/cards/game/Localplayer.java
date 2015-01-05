package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.ingame.GameActivity;

public class Localplayer extends Player {
	
	GameActivity conn;

	/**
	 * @param n
	 * @param m
	 * @param conn
	 */
	public Localplayer(String n, int m, GameActivity conn) {
		super(n, m);
		setConn(conn);
	}

	public GameActivity getConn() {
		return conn;
	}

	public void setConn(GameActivity conn) {
		this.conn = conn;
	}

	@Override
	public void connect(String msg){
		conn.checkMessage(msg);
	}	
}
