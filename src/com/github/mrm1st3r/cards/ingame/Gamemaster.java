package com.github.mrm1st3r.cards.ingame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.BluetoothConnection;
import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.*;

public class Gamemaster extends GameActivity {

	int max = 0;

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);

		max = ((Cards) getApplication()).connections.size() + 1;

		SharedPreferences pref = getSharedPreferences(
				getString(R.string.pref_file), Context.MODE_PRIVATE);
		String name = pref.getString(MainActivity.PREF_PLAYER_NAME, "");

		ThirtyOne game = new ThirtyOne(max);
		game.addPlayer(new Localplayer(name, 3, this));		

		for (BluetoothConnection conn : ((Cards) getApplication()).connections
				.keySet()) {
			AsyncBluetoothConnection asConn = (AsyncBluetoothConnection) conn;
			asConn.unpause();
			String name1 = ((Cards) getApplication()).connections.get(conn);
			game.addPlayer(new Bluetoothplayer(name1, 3, asConn));
		}
		game.start();
	}
}