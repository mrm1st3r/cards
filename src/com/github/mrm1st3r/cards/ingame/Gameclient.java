package com.github.mrm1st3r.cards.ingame;

import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.connection.AsyncBluetoothConnection;
import com.github.mrm1st3r.connection.OnMessageReceivedHandler;

public class Gameclient extends GameActivity{

	AsyncBluetoothConnection connection = null;
	
	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
		
		connection = (AsyncBluetoothConnection) ((Cards) getApplication()).connections.keySet().iterator().next();

		connection.setReceiveHandler(new OnMessageReceivedHandler() {
			@Override
			public void onMessageReceived(final String msg) {
				// logik
				Gameclient.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						checkMessage(msg);// auf die Oberfläche zugreifen
					}
				});
			}
		});
		connection.unpause();
	}

	@Override
	public void sendMessage(String msg) {
		connection.write(msg);		
	}
}
