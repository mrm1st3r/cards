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
 */
public class ClientGameActivity extends GameActivity {

	private SimpleBluetoothConnection connection = null;
	private AlertDialog quitDialog = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setPlayerList((Collection<String>) getIntent().getExtras().getSerializable(LobbyActivity.EXTRA_PLAYER_LIST));

		connection = ((Cards) getApplication()).getConnections().keySet().iterator().next();

		connection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(AsynchronousConnection<String> conn, final String msg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						handleMessage(msg);
					}
				});
			}
		});
		connection.unpause();
		connection.setOnConnectionChangeHandler(
				new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(ThreadedConnection tc) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						leaveGame();
					}
				});
			}
		});
	}

	private void leaveGame() {
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);
		String name = pref.getString(Cards.PREF_PLAYER_NAME, "");
		connection.write("left " + name);
		connection.close();
		ClientGameActivity.super.onBackPressed();
	}

	@Override
	public void sendMessage(String msg) {
		connection.write(msg);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.leave_game);
		dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				quitDialog.dismiss();
				leaveGame();
			}
		});
		dialog.setNegativeButton(R.string.no, null);
		quitDialog = dialog.create();
		quitDialog.show();
	}
}
