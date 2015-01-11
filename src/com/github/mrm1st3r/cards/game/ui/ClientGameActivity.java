package com.github.mrm1st3r.cards.game.ui;

import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * This is the user interface started on the client devices
 * which only receives it's information from the host.
 * @author Sergius Maier
 * @version 1.0
 */
public class ClientGameActivity extends GameActivity {

	/**
	 * Bluetooth connection to the host.
	 */
	private SimpleBluetoothConnection connection = null;
	
	@Override
	public final void onCreate(final Bundle bun) {
		super.onCreate(bun);
		
		connection = (SimpleBluetoothConnection) ((Cards) getApplication()).
				getConnections().keySet().iterator().next();

		connection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {

				ClientGameActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						checkMessage(msg);
					}
				});
			}
		});
		connection.unpause();
	}

	@Override
	public final void sendMessage(final String msg) {
		connection.write(msg);		
	}
}
