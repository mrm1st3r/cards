package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.ingame.GameActivity;

public class Localplayer extends Player {
	
	GameActivity gameAct;

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
		return gameAct;
	}

	public void setConn(GameActivity conn) {
		this.gameAct = conn;
	}

	@Override
	public void connect(final String msg){
		gameAct.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gameAct.checkMessage(msg);
			}
		});
	}	
}
