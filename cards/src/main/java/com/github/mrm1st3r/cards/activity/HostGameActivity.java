package com.github.mrm1st3r.cards.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.BluetoothPlayer;
import com.github.mrm1st3r.cards.game.LocalPlayer;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.libdroid.display.BitmapUtil;

/**
 * This is the user interface that is started on the host device and controls the game loop.
 */
public class HostGameActivity extends GameActivity {

	private static final String TAG = HostGameActivity.class.getSimpleName();
	private Thread gameThread;
	private ThirtyOne game;
	private LocalPlayer localPlayer;
	private AlertDialog quitDialog = null;

	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		newGame();
	}

	public void newGame() {
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE, MODE_PRIVATE);
		String localName = pref.getString(Cards.PREF_PLAYER_NAME, "");
		HashMap<SimpleBluetoothConnection, String> connections = ((Cards) getApplication()).getConnections();
		setPlayerList(connections.values());

		int playerCount = connections.size() + 1;
		Log.d(TAG, "starting new game with " + playerCount + " players");

		game = ThirtyOne.createInstance(playerCount);
		localPlayer = new LocalPlayer(localName, this);
		game.addPlayer(localPlayer);

		for (SimpleBluetoothConnection conn : connections.keySet()) {
			conn.unpause();
			String remoteName = connections.get(conn);
			final BluetoothPlayer remotePlayer = new BluetoothPlayer(remoteName, conn);

			game.addPlayer(remotePlayer);
			remotePlayer.getConnection().setOnReceivedHandler(
					new OnReceivedHandler<String>() {
						@Override
						public void onReceived(
								final AsynchronousConnection<String> conn,
								final String msg) {
							game.handleMessage(remotePlayer, msg);
						}
					});
		}

		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				game.start();
			}
		});
		gameThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread th, Throwable e) {
				Log.w(TAG, e);
				Toast.makeText(HostGameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				closeGame();
			}
		});
		gameThread.setName("game_loop");
		gameThread.start();
	}

	@SuppressWarnings("deprecation")
	private void closeGame() {
		Log.d(TAG, "closing the game");
		for (SimpleBluetoothConnection c : ((Cards) getApplication()).getConnections().keySet()) {
			c.write("quit");
			c.close();
		}
		try {
			game.getPlayers().clear();
			game = null;
			gameThread.stop();
		} catch (Exception e) {
			Log.w(TAG, e);
		}
		((Cards) getApplication()).getConnections().clear();
		BitmapUtil.clearBitmapBuffer();
		super.onBackPressed();
		finish();
	}

	@Override
	public void sendMessage(String msg) {
		game.handleMessage(localPlayer, msg);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.close_game);
		dialog.setMessage(R.string.close_game_info);
		dialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						quitDialog.dismiss();
						closeGame();
					}
				});
		dialog.setNegativeButton(R.string.no, null);
		quitDialog = dialog.create();
		quitDialog.show();
	}
}
