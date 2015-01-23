package com.github.mrm1st3r.cards.game.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.BluetoothPlayer;
import com.github.mrm1st3r.cards.game.LocalPlayer;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.util.BitmapUtil;

/**
 * This is the user interface that is started on the host device and controls
 * the game loop.
 * 
 * @author Sergius Maier
 * @version 1.0
 */
public class HostGameActivity extends GameActivity {

	/**
	 * Debug Tag.
	 */
	private static final String TAG = HostGameActivity.class.getSimpleName();
	/**
	 * The background thread where the game loop is running.
	 */
	private Thread gameThread;
	/**
	 * The game itself.
	 */
	private ThirtyOne game;
	/**
	 * The local player.
	 */
	private LocalPlayer localPlayer;
	/**
	 * Dialog that is shown when the back key is pressed.
	 */
	private AlertDialog quitDialog = null;

	@Override
	public final void onCreate(final Bundle bun) {
		super.onCreate(bun);
		newGame();
	}

	/**
	 * Start a new game.
	 */
	public final void newGame() {

		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE,
				Context.MODE_PRIVATE);
		String localName = pref.getString(Cards.PREF_PLAYER_NAME, "");

		HashMap<SimpleBluetoothConnection, String> connections =
				((Cards) getApplication()).getConnections();

		// add one for local player
		int playerCount = connections.size() + 1;

		Log.d(TAG, "starting new game with " + playerCount + " players");
		game = new ThirtyOne(playerCount);
		localPlayer = new LocalPlayer(localName.replace(' ', '_'),
				ThirtyOne.HAND_SIZE, ThirtyOne.MAX_LIFES, this);
		game.addPlayer(localPlayer);

		for (SimpleBluetoothConnection conn : connections.keySet()) {
			conn.unpause();
			String remoteName = connections.get(conn);
			final BluetoothPlayer remotePlayer = new BluetoothPlayer(
					remoteName.replace(' ', '_'), ThirtyOne.HAND_SIZE,
					ThirtyOne.MAX_LIFES, conn);

			game.addPlayer(remotePlayer);
			remotePlayer.getConn().setOnReceivedHandler(
					new OnReceivedHandler<String>() {
						@Override
						public void onReceived(
								final AsynchronousConnection<String> conn,
								final String msg) {
							game.checkMessage(remotePlayer, msg);
						}
					});
		}
		
		// Run the game loop in an own thread to avoid the user interface being
		// not usable.
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				game.start();
			}
		});

		gameThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread th, final Throwable e) {
				Log.w(TAG, e);
				closeGame();
			}
		});

		// for debugging uses
		gameThread.setName("game_loop");

		gameThread.start();
	}

	/**
	 * Close the current game and disconnect all players.
	 */
	@SuppressWarnings("deprecation")
	private void closeGame() {
		Log.d(TAG, "closing the game");
		for (SimpleBluetoothConnection c : ((Cards) getApplication())
				.getConnections().keySet()) {

			c.write("quit");
			c.close();
		}
		// hard-abort the game thread as otherwise the game round would have
		// to be played until the end
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
	public final void sendMessage(final String msg) {
		game.checkMessage(localPlayer, msg);
	}

	@Override
	public final void onBackPressed() {
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
