package com.github.mrm1st3r.cards.ingame;

import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

public class Gameclient extends GameActivity{

	SimpleBluetoothConnection connection = null;
	
	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
		
		connection = (SimpleBluetoothConnection) ((Cards) getApplication()).
				connections.keySet().iterator().next();

		connection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {
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
