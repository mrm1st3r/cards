package com.github.mrm1st3r.cards.activity;

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
import com.github.mrm1st3r.cards.lobby.LobbyFragment;
import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;

/**
 * This activity shows all players who are connected in this lobby and will
 * start the actual game activity when receiving the appropriate command from
 * the game host.
 */
public class LobbyActivity extends Activity {

	public static final String EXTRA_PLAYER_LIST = "EXTRA_PLAYER_LIST";
	public static final String EXTRA_LOCAL_NAME  = "EXTRA_LOCAL_NAME";

	private static final String TAG = LobbyActivity.class.getSimpleName();

	private SimpleBluetoothConnection connection = null;
	private LinkedList<String> connectedPlayers = new LinkedList<>();
	private ArrayAdapter<String> connectedPlayersAdapter;
	private String localPlayerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		initUi();
		setupConnection();
	}

	private void initUi() {
		connectedPlayersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectedPlayers);
		LobbyFragment lobFrag = (LobbyFragment) getFragmentManager().findFragmentById(R.id.player_list);
		lobFrag.setAdapter(connectedPlayersAdapter);
	}

	private void setupConnection() {
		connection = ((Cards) getApplication()).getConnections().keySet().iterator().next();
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);
		localPlayerName = pref.getString(Cards.PREF_PLAYER_NAME, "");
		connection.write("join " + localPlayerName);

		connection.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(AsynchronousConnection<String> ac, String msg) {
				handleIncomingMessage(msg);
			}
		});

		connection.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(ThreadedConnection tc) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onBackPressed();
					}
				});
			}
		});
		connection.start();
	}

	private void handleIncomingMessage(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String[] set = msg.split(" ");
				switch (set[0]) {
					case "join":
						connectedPlayers.add(set[1]);
						connectedPlayersAdapter.notifyDataSetChanged();
						break;
					case "left":
						connectedPlayers.remove(set[0]);
						connectedPlayersAdapter.notifyDataSetChanged();
						break;
					case "start":
						Intent intent = new Intent(LobbyActivity.this, ClientGameActivity.class);
						intent.putExtra(EXTRA_PLAYER_LIST, connectedPlayers);
						intent.putExtra(EXTRA_LOCAL_NAME, localPlayerName);
						connection.setOnConnectionChangeHandler(null);
						connection.pause();
						startActivity(intent);
						finish();
						break;
					case "quit":
						onBackPressed();
						break;
				}
			}
		});
	}

	private void leaveLobby() {
		Log.d(TAG, "leaving lobby");
		if (connection != null) {
			connection.setOnConnectionChangeHandler(null);
			connection.close();
			connection = null;
		}
		((Cards) getApplication()).getConnections().clear();
		connectedPlayers.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		leaveLobby();
	}
}
