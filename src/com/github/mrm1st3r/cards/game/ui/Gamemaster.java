package com.github.mrm1st3r.cards.game.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.Bluetoothplayer;
import com.github.mrm1st3r.cards.game.Localplayer;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

public class Gamemaster extends GameActivity {

	private static final String TAG = Gamemaster.class.getSimpleName();
	
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
		max = ((Cards) getApplication()).getConnections().size() + 1;

		SharedPreferences pref = getSharedPreferences(
				Cards.PREF_FILE, Context.MODE_PRIVATE);
		String localName = pref.getString(Cards.PREF_PLAYER_NAME, "");

		Log.d(TAG, "starting new game with " + max + " players");
		game = new ThirtyOne(max);
		game.addPlayer(new Localplayer(localName, 3, 3, this));

		for (SimpleBluetoothConnection conn : ((Cards) getApplication())
				.getConnections().keySet()) {

			conn.unpause();
			String remoteName = ((Cards) getApplication()).getConnections().get(conn);
			game.addPlayer(new Bluetoothplayer(remoteName, 3, 3, conn));
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
