package com.github.mrm1st3r.cards.lobby;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.ui.Gameclient;
import com.github.mrm1st3r.connection.AsynchronousConnection;
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.OnReceivedHandler;
import com.github.mrm1st3r.connection.ThreadedConnection;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

/**
 * This activity shows all players who are connected in this lobby
 * and will start the actual game activity when receiving the
 * appropriate command from the game host.
 * 
 * @author Lukas 'mrm1st3r' Taake
 *
 */
public class LobbyActivity extends Activity {

	/**
	 * Debug tag.
	 */
	private static final String TAG = LobbyActivity.class.getSimpleName();
	/**
	 * Used as a extra field to hand over the player list to the game activity.
	 */
	public static final String EXTRA_PLAYER_LIST = "EXTRA_PLAYER_LIST";
	/**
	 * Bluetooth connection to game host.
	 */
	private SimpleBluetoothConnection conn = null;
	/**
	 * List of all connected players.
	 */
	private LinkedList<String> playerList = new LinkedList<String>();
	/**
	 * Adapter for {@link #playerList}.
	 */
	private ArrayAdapter<String> playerListAdapter;
	/**
	 * Local player name.
	 */
	private String name;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		
		// initialize user interface
		
		playerListAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, playerList);
		LobbyFragment lobFrag = (LobbyFragment) getFragmentManager().
				findFragmentById(R.id.player_list);
		lobFrag.setAdapter(playerListAdapter);
		
		// get connection to host from application
		conn = ((Cards) getApplication()).getConnections().keySet().
				iterator().next();

		SharedPreferences pref = getSharedPreferences(
				Cards.PREF_FILE, Context.MODE_PRIVATE);
		name = pref.getString(Cards.PREF_PLAYER_NAME, "");

		// send own name to host
		conn.write("join " + name);
		
		// register new receive handler for incoming data
		conn.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> ac,
					final String msg) {
				handleIncomingMessage(msg);
			}
		});

		conn.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(final ThreadedConnection tc) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onBackPressed();
					}
				});
			}
		});
		conn.start();
	}

	/**
	 * Handle incoming messages.
	 * @param msg incoming mesage
	 */
	private void handleIncomingMessage(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String[] set = msg.split(" ");

				if (set[0].equals("join")) {
					playerList.add(set[1]);
					playerListAdapter.notifyDataSetChanged();
					
				} else if (set[0].equals("left")) {
					playerList.remove(set[0]);
					playerListAdapter.notifyDataSetChanged();
					
				} else if (set[0].equals("start")) {
					
					Intent intent = new Intent(LobbyActivity.this,
							Gameclient.class);
					intent.putExtra(EXTRA_PLAYER_LIST, playerList);
					startActivity(intent);
					finish();
				} else if (set[0].equals("quit")) {
					onBackPressed();
				}
			}
		});
	}

	/**
	 * Close connection to game host and unregister connection change handler
	 * to prevent any double quitting.
	 */
	private void leaveLobby() {
		Log.d(TAG, "leaving lobby");
		
		if (conn != null) {
			conn.setOnConnectionChangeHandler(null);
			conn.close();
			conn = null;
		}
		playerList.clear();
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
		leaveLobby();
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public final void onBackPressed() {
		super.onBackPressed();
		leaveLobby();
	}
}
