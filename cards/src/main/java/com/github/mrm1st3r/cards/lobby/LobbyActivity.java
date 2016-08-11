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
import com.github.mrm1st3r.cards.game.ui.ClientGameActivity;
import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

/**
 * This activity shows all players who are connected in this lobby and will
 * start the actual game activity when receiving the appropriate command from
 * the game host.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public class LobbyActivity extends Activity {

	/**
	 * Debug tag.
	 */
	private static final String TAG = LobbyActivity.class.getSimpleName();
	/**
	 * Extra field to hand over the player list to the game activity.
	 */
	public static final String EXTRA_PLAYER_LIST = "EXTRA_PLAYER_LIST";
	/**
	 * Extra field for local player name.
	 */
	public static final String EXTRA_LOCAL_NAME  = "EXTRA_LOCAL_NAME";
	/**
	 * Bluetooth connection to game host.
	 */
	private SimpleBluetoothConnection mConn = null;
	/**
	 * List of all connected players.
	 */
	private LinkedList<String> mPlayerList = new LinkedList<String>();
	/**
	 * Adapter for {@link #mPlayerList}.
	 */
	private ArrayAdapter<String> mPlayerListAdapter;
	/**
	 * Local player name.
	 */
	private String mLocalName;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);

		// initialize user interface

		mPlayerListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mPlayerList);
		LobbyFragment lobFrag = (LobbyFragment) getFragmentManager()
				.findFragmentById(R.id.player_list);
		lobFrag.setAdapter(mPlayerListAdapter);

		// get connection to host from application
		mConn = ((Cards) getApplication()).getConnections().keySet().iterator()
				.next();

		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE,
				Context.MODE_PRIVATE);
		mLocalName = pref.getString(Cards.PREF_PLAYER_NAME, "");

		// send own name to host
		mConn.write("join " + mLocalName);

		// register new receive handler for incoming data
		mConn.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(final AsynchronousConnection<String> ac,
					final String msg) {
				handleIncomingMessage(msg);
			}
		});

		mConn.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
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
		mConn.start();
	}

	/**
	 * Handle incoming messages.
	 * 
	 * @param msg
	 *            incoming message
	 */
	private void handleIncomingMessage(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String[] set = msg.split(" ");

				if (set[0].equals("join")) {
					mPlayerList.add(set[1]);
					mPlayerListAdapter.notifyDataSetChanged();

				} else if (set[0].equals("left")) {
					mPlayerList.remove(set[0]);
					mPlayerListAdapter.notifyDataSetChanged();

				} else if (set[0].equals("start")) {

					Intent intent = new Intent(LobbyActivity.this,
							ClientGameActivity.class);
					intent.putExtra(EXTRA_PLAYER_LIST, mPlayerList);
					intent.putExtra(EXTRA_LOCAL_NAME, mLocalName);
					mConn.setOnConnectionChangeHandler(null);
					mConn.pause();
					startActivity(intent);
					finish();
				} else if (set[0].equals("quit")) {
					onBackPressed();
				}
			}
		});
	}

	/**
	 * Close connection to game host and unregister connection change handler to
	 * prevent any double quitting.
	 */
	private void leaveLobby() {
		Log.d(TAG, "leaving lobby");

		if (mConn != null) {
			mConn.setOnConnectionChangeHandler(null);
			mConn.close();
			mConn = null;
		}
		((Cards) getApplication()).getConnections().clear();
		mPlayerList.clear();
	}

	@Override
	public final void onDestroy() {
		super.onDestroy();
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
