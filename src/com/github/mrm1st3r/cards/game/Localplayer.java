package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.ingame.GameActivity;

public class Localplayer extends Player {
	
	GameActivity gameAct;

	/**
	 * @param n
	 * @param m
	 * @param conn
	 */
	public Localplayer(String n, int m, int l, GameActivity ga) {
		super(n, m, l);
		setGameAct(ga);
	}

	public GameActivity getGameAct() {
		return gameAct;
	}

	public void setGameAct(GameActivity ga) {
		this.gameAct = ga;
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
