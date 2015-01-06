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
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.ingame.Gameclient;
import com.github.mrm1st3r.connection.AsyncBluetoothConnection;
import com.github.mrm1st3r.connection.BluetoothConnection;
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.OnMessageReceivedHandler;

public class LobbyActivity extends Activity {

	private static final String TAG = LobbyActivity.class.getSimpleName();
	
	public static final String EXTRA_PLAYER_LIST = "EXTRA_PLAYER_LIST";
	
	private AsyncBluetoothConnection connection = null;
	private LinkedList<String> playerList = new LinkedList<String>();
	private ArrayAdapter<String> playerListAdapter;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		playerListAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, playerList);
		LobbyFragment lobFrag = (LobbyFragment)getFragmentManager().
				findFragmentById(R.id.player_list);
		lobFrag.setAdapter(playerListAdapter);
		
		// get connection to host from application
		connection = (AsyncBluetoothConnection) ((Cards) getApplication()).connections.keySet().iterator().next();

		SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
		name = pref.getString(MainActivity.PREF_PLAYER_NAME, "");

		// send own name to host
		connection.write("join " + name);
		
		// register new receive handler for incoming data
		connection.setReceiveHandler(new OnMessageReceivedHandler() {
			@Override
			public void onMessageReceived(final String msg) {
				LobbyActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String name = msg.substring(5);
						Log.d(TAG, "received: " + msg + ", name: " + name);
						if (msg.startsWith("join")) {
							playerList.add(name);
							playerListAdapter.notifyDataSetChanged();
						} else if (msg.startsWith("left")) {
							playerList.remove(name);
							playerListAdapter.notifyDataSetChanged();
						} else if (msg.equals("start")) {
							Intent intent = new Intent(LobbyActivity.this,
									Gameclient.class);
							intent.putExtra(EXTRA_PLAYER_LIST, playerList);
							startActivity(intent);
						}
						
					}
				});
			}
		});
		connection.setOnDisconnectHandler(new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(BluetoothConnection conn) {
				Intent i = new Intent(LobbyActivity.this, LobbyJoinActivity.class);
				startActivity(i);
			}
		});
		connection.unpause();
	}
	
	private void leaveLobby() {
		if (connection != null) {
			connection.close();
			connection = null;
		}
		playerList.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		leaveLobby();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		leaveLobby();
	}
}
