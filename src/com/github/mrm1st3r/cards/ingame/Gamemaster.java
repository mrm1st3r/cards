package com.github.mrm1st3r.cards.ingame;

import android.os.Bundle;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.*;

public class Gamemaster extends GameActivity {
	
	int max = 0;

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
		
		bun = getIntent().getExtras();
		max = bun.getInt("players");
		
		ThirtyOne game = new ThirtyOne(max);
		for (int i = 0; i < max; i++){
			game.addPlayer(new Player(bun.getString("player"+i), 3));
		}
		game.start();
		
		
	}
}
