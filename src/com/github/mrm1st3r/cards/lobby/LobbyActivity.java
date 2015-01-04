package com.github.mrm1st3r.cards.lobby;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.BluetoothConnection;
import com.github.mrm1st3r.btutil.OnDisconnectHandler;
import com.github.mrm1st3r.btutil.OnMessageReceivedHandler;
import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;

public class LobbyActivity extends Activity {

	private static final String TAG = LobbyActivity.class.getSimpleName();
	
	private AsyncBluetoothConnection connection = null;
	private LinkedList<String> players = new LinkedList<String>();
	private ArrayAdapter<String> playerListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		playerListAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, players);
		LobbyFragment lobFrag = (LobbyFragment)getFragmentManager().
				findFragmentById(R.id.player_list);
		lobFrag.setAdapter(playerListAdapter);
		
		connection = (AsyncBluetoothConnection) ((Cards) getApplication()).connections.keySet().iterator().next();

		connection.setReceiveHandler(new OnMessageReceivedHandler() {
			@Override
			public void onMessageReceived(final String msg) {
				LobbyActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String name = msg.substring(5);
						Log.d(TAG, "received: " + msg + ", name: " + name);
						if (msg.startsWith("join")) {
							players.add(name);
							playerListAdapter.notifyDataSetChanged();
						} else if (msg.startsWith("left")) {
							players.remove(name);
							playerListAdapter.notifyDataSetChanged();
						}
						
					}
				});
			}
		});
		connection.setOnDisconnectHandler(new OnDisconnectHandler() {
			@Override
			public void onDisconnect(BluetoothConnection conn) {
				Intent i = new Intent(LobbyActivity.this, LobbyJoinActivity.class);
				startActivity(i);
			}
		});
		connection.unpause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		connection.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
