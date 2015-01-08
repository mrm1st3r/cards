package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.ingame.*;

public class Localplayer extends Player {
	
	Gamemaster gameAct;

	/**
	 * @param n
	 * @param m
	 * @param conn
	 */
	public Localplayer(String n, int m, int l, Gamemaster ga) {
		super(n, m, l);
		setGameAct(ga);
	}

	public Gamemaster getGameAct() {
		return gameAct;
	}

	public void setGameAct(Gamemaster  ga) {
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
	
	public void newGame(){
		gameAct.newGame();
	}
}
