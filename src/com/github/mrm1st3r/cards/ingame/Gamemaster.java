package com.github.mrm1st3r.cards.ingame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.*;
import com.github.mrm1st3r.connection.AsyncBluetoothConnection;
import com.github.mrm1st3r.connection.BluetoothConnection;

public class Gamemaster extends GameActivity {

	int max = 0;
	private Thread gameThread;
	private ThirtyOne game;

	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
		newGame();
	}
	
	public void newGame(){
		max = ((Cards) getApplication()).connections.size() + 1;

		SharedPreferences pref = getSharedPreferences(
				getString(R.string.pref_file), Context.MODE_PRIVATE);
		String name = pref.getString(MainActivity.PREF_PLAYER_NAME, "");

		game = new ThirtyOne(max);
		game.addPlayer(new Localplayer(name, 3, this));		

		for (BluetoothConnection conn : ((Cards) getApplication()).connections
				.keySet()) {
			AsyncBluetoothConnection asConn = (AsyncBluetoothConnection) conn;
			asConn.unpause();
			String name1 = ((Cards) getApplication()).connections.get(conn);
			game.addPlayer(new Bluetoothplayer(name1, 3, asConn));
		}
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				game.start();
			}
		});
		gameThread.start();
	}

	@Override
	public void sendMessage(String msg) {
		game.checkMessage(msg);	
	}
}
