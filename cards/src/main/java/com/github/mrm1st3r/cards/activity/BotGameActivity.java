package com.github.mrm1st3r.cards.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.ComputerPlayer;
import com.github.mrm1st3r.cards.game.LocalPlayer;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.libdroid.display.BitmapUtil;

/**
 * This is the user interface used for playing a game against a computer player.
 */
public class BotGameActivity extends GameActivity {

	private static final String TAG = BotGameActivity.class.getSimpleName();

	private Thread gameThread;
	private ThirtyOne game;
	private LocalPlayer localPlayer;
	private AlertDialog quitDialog = null;

	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		newGame();
	}

	public final void newGame() {
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);
		String localName = pref.getString(Cards.PREF_PLAYER_NAME, "");
		int playerCount = 2;
		Log.d(TAG, "starting new game with " + playerCount + " players");
		game = new ThirtyOne(playerCount);
		localPlayer = new LocalPlayer(localName, ThirtyOne.HAND_SIZE, ThirtyOne.MAX_LIFES, this);
		game.addPlayer(localPlayer);
		ComputerPlayer bot = new ComputerPlayer("Bot 1", ThirtyOne.HAND_SIZE, ThirtyOne.MAX_LIFES);
		bot.setGame(game);
		game.addPlayer(bot);
		LinkedList<String> players = new LinkedList<String>();
		players.add(bot.getName());
		players.add(localName);
		setPlayerList(players);

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
		gameThread.setName("game_loop");
		gameThread.start();
	}

	@Override
	public void sendMessage(String msg) {
		game.handleMessage(localPlayer, msg);
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
