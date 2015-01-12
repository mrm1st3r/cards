package com.github.mrm1st3r.cards.game.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.game.BluetoothPlayer;
import com.github.mrm1st3r.cards.game.LocalPlayer;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * This is the user interface that is started on the host device and
 * controls the game loop.
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

	@Override
	public final void onCreate(final Bundle bun) {
		super.onCreate(bun);		
		newGame();
	}
	
	/**
	 * Start a new game.
	 */
	public final void newGame() {
		int playerCount =
				((Cards) getApplication()).getConnections().size() + 1;
		SharedPreferences pref = getSharedPreferences(
				Cards.PREF_FILE, Context.MODE_PRIVATE);
		String localName = pref.getString(Cards.PREF_PLAYER_NAME, "");
		Log.d(TAG, "starting new game with " + playerCount + " players");
		game = new ThirtyOne(playerCount);
		localPlayer = new LocalPlayer(localName, ThirtyOne.HAND_SIZE,
				ThirtyOne.MAX_LIFES, this);
		game.addPlayer(localPlayer);
		for (SimpleBluetoothConnection conn : ((Cards) getApplication())
				.getConnections().keySet()) {
			conn.unpause();
			String remoteName =
					((Cards) getApplication()).getConnections().get(conn);
			final BluetoothPlayer remotePlayer = new BluetoothPlayer(remoteName,
					ThirtyOne.HAND_SIZE, ThirtyOne.MAX_LIFES, conn);			
			game.addPlayer(remotePlayer);			
			remotePlayer.getConn()
					.setOnReceivedHandler(new OnReceivedHandler<String>() {
				@Override
				public void onReceived(
						final AsynchronousConnection<String> conn,
						final String msg) {
					synchronized (remotePlayer.getLock()) {
						game.checkMessage(msg);
						remotePlayer.getLock().notify();
					}
				}
			});
		}
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				game.start();
			}
		});
		gameThread.setName("Game loop");
		gameThread.start();
	}

	@Override
	public final void sendMessage(final String msg) {
		synchronized (localPlayer.getLock()) {
			game.checkMessage(msg);
			localPlayer.getLock().notify();
		}
	}
}
