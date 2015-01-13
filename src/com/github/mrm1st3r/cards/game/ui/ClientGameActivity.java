package com.github.mrm1st3r.cards.game.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.ThreadedConnection;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * This is the user interface started on the client devices which only receives
 * it's information from the host.
 * 
 * @author Sergius Maier, Lukas Taake
 * @version 1.0
 */
public class ClientGameActivity extends GameActivity {

	/**
	 * Bluetooth connection to the host.
	 */
	private SimpleBluetoothConnection connection = null;
	/**
	 * Dialog that is shown when the back key is pressed.
	 */
	private AlertDialog quitDialog = null;

	@Override
	public final void onCreate(final Bundle bun) {
		super.onCreate(bun);

		connection = ((Cards) getApplication())
				.getConnections().keySet().iterator().next();

		connection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						checkMessage(msg);
					}
				});
			}
		});
		connection.unpause();
		connection.setOnConnectionChangeHandler(
				new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(final ThreadedConnection tc) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						leaveGame();
					}
				});
			}
		});
	}

	/**
	 * Close the connection to the host and leave the game.
	 */
	private void leaveGame() {
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE,
				Context.MODE_PRIVATE);
		String name = pref.getString(Cards.PREF_PLAYER_NAME, "");
		
		connection.write("left " + name);
		connection.close();
		ClientGameActivity.super.onBackPressed();
	}
	
	@Override
	public final void sendMessage(final String msg) {
		connection.write(msg);
	}

	@Override
	public final void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.leave_game);
		dialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				quitDialog.dismiss();
				leaveGame();
			}
		});
		dialog.setNegativeButton(R.string.no, null);
		quitDialog = dialog.create();
		quitDialog.show();
	}
}
