package com.github.mrm1st3r.cards.lobby;

import java.util.HashMap;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.ingame.Gamemaster;
import com.github.mrm1st3r.connection.AsyncBluetoothConnection;
import com.github.mrm1st3r.connection.BluetoothConnection;
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.OnMessageReceivedHandler;
import com.github.mrm1st3r.connection.ServerThread;
import com.github.mrm1st3r.connection.bluetooth.BluetoothUtil;
import com.github.mrm1st3r.util.HashMapAdapter;
import com.github.mrm1st3r.util.ResultAction;

public class LobbyCreateActivity extends Activity {

	private static final String TAG = LobbyCreateActivity.class.getSimpleName();
	private static final int LOBBY_CREATE_TIMEOUT = 60;

	private ServerThread serv = null;

	private HashMap<BluetoothConnection, String> playerList=
			new HashMap<BluetoothConnection, String>();
	private HashMapAdapter<BluetoothConnection, String> playerListAdapter = null;
	
	private Button btnStart = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// go back to start if bluetooth is not supported
		if (!BluetoothUtil.isSupported()) {
			Toast.makeText(this, getString(
					R.string.bluetooth_not_supported), Toast.LENGTH_LONG)
					.show();
			onBackPressed();
		}

		BluetoothUtil.enable(this, new ResultAction() {

			@Override
			public void onSuccess() {

				BluetoothUtil.enableDiscoverable(LobbyCreateActivity.this,
						LOBBY_CREATE_TIMEOUT, new ResultAction() {
					@Override
					public void onSuccess() {
						createLobby();
					}
					@Override
					public void onFailure() {
						onBackPressed();
					}
				});
			}
			@Override
			public void onFailure() {
				onBackPressed();
			}
		});

		setContentView(R.layout.activity_lobby_create);
		((TextView)findViewById(R.id.txtLobbyName)).setText(BluetoothUtil.getDeviceName());
	}
	
	private void createLobby() {
		btnStart = (Button) findViewById(R.id.btnStart);
		playerListAdapter = new HashMapAdapter<BluetoothConnection, String>(
				LobbyCreateActivity.this, playerList) {
			@Override
			public View getView(int pos, View convertView,
					ViewGroup parent) {
				TextView rowView;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) context
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = (TextView) inflater.inflate(
							android.R.layout.simple_list_item_1, parent, false);
				} else {
					rowView = (TextView) convertView;
				}

				rowView.setText(getItem(pos));

				return rowView;
			}

		};
		LobbyFragment lobFrag = (LobbyFragment)getFragmentManager().
				findFragmentById(R.id.player_list);
		lobFrag.setAdapter(playerListAdapter);
		
		serv = new ServerThread(LobbyCreateActivity.this, new OnConnectionChangeHandler() {
			@Override
			public void onConnect(final BluetoothConnection conn) {
				clientConnected(conn);
			}
			@Override
			public void onConnectionFailed(BluetoothDevice dev) {
				
			}
		});
		serv.start();

	}

	private void clientConnected(BluetoothConnection conn) {
		// new connection for each player
		final AsyncBluetoothConnection asConn = (AsyncBluetoothConnection) conn;
		
		asConn.setReceiveHandler(new OnMessageReceivedHandler() {
			@Override
			public void onMessageReceived(final String msg) {
				
				// send player list to new player
				for (String player : playerList.values()) {
					asConn.write("join " + player);
				}
				// send host name to new player
				SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
				String hostName = pref.getString(MainActivity.PREF_PLAYER_NAME, "");
				asConn.write("join " + hostName);

				String name = msg.substring(5);
				Log.d(TAG, "received: " + msg + ", name: " + name);
				if (msg.startsWith("join")) {
					playerList.put(asConn, name);
				} else if(msg.startsWith("left")) {
					playerList.remove(asConn);
				}
				
				LobbyCreateActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						btnStart.setEnabled(true);
						Log.d(TAG, "received: " + msg);
						playerListAdapter.notifyDataSetChanged();
					}
				});
				
				// send new player name to other players
				for (BluetoothConnection c : playerList.keySet()) {
					((AsyncBluetoothConnection)c).write(msg);
				}
			}
		});
		asConn.setOnDisconnectHandler(new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(BluetoothConnection conn) {
				playerList.remove(asConn);
				LobbyCreateActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						playerListAdapter.notifyDataSetChanged();
						
						// can't start game with no other players
						if (playerList.size() == 0) {
							btnStart.setEnabled(false);
						}
					}
				});
				// send leave note to other player
				for (BluetoothConnection c : playerList.keySet()) {
					((AsyncBluetoothConnection)c).write("left " + playerList.get(conn));
				}
			}
		});
	}

	public void start(View v) {
		((Cards)getApplication()).connections = playerList;
		
		// send start command to clients and pause connections
		for (BluetoothConnection conn : playerList.keySet()) {
			AsyncBluetoothConnection asConn = (AsyncBluetoothConnection)conn;
			asConn.write("start");
			asConn.pause();
		}
		// stop listening for new connections.
		serv.cancel();
		
		Intent intent = new Intent(this, Gamemaster.class);
		startActivity(intent);
	}

	private void cancelLobby() {
		if (serv != null) {
			serv.cancel();
		}
		for (BluetoothConnection conn : playerList.keySet()) {
			conn.close();
		}
		playerList.clear();
	}

	public void becomeVisible(MenuItem item) {
		BluetoothUtil.enableDiscoverable(this, LOBBY_CREATE_TIMEOUT, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelLobby();
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if (BluetoothUtil.onActivityResult(this, reqCode, resultCode, data)) {
			// Bluetooth results should be covered by BtUtil.
			return;
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		cancelLobby();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.lobby_create, menu);
	    return true;
	}
}
