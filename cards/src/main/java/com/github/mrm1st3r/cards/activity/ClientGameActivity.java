package com.github.mrm1st3r.cards.activity;

import java.util.Collection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

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
	private SimpleBluetoothConnection mConnection = null;
	/**
	 * Dialog that is shown when the back key is pressed.
	 */
	private AlertDialog mQuitDialog = null;

	@SuppressWarnings("unchecked")
	@Override
	public final void onCreate(final Bundle bun) {
		super.onCreate(bun);
		
		setPlayerList((Collection<String>) getIntent().getExtras()
				.getSerializable(LobbyActivity.EXTRA_PLAYER_LIST));

		mConnection = ((Cards) getApplication())
				.getConnections().keySet().iterator().next();

		mConnection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> conn,
					final String msg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						handleMessage(msg);
					}
				});
			}
		});
		mConnection.unpause();
		mConnection.setOnConnectionChangeHandler(
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
		
		mConnection.write("left " + name);
		mConnection.close();
		ClientGameActivity.super.onBackPressed();
	}
	
	@Override
	public final void sendMessage(final String msg) {
		mConnection.write(msg);
	}

	@Override
	public final void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.leave_game);
		dialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				mQuitDialog.dismiss();
				leaveGame();
			}
		});
		dialog.setNegativeButton(R.string.no, null);
		mQuitDialog = dialog.create();
		mQuitDialog.show();
	}
}
