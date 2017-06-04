package com.github.mrm1st3r.cards.activity;

import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
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
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.lobby.LobbyFragment;
import com.github.mrm1st3r.libdroid.connect.AsynchronousConnection;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.OnReceivedHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;
import com.github.mrm1st3r.libdroid.connect.bluetooth.BluetoothUtil;
import com.github.mrm1st3r.libdroid.connect.bluetooth.ServerThread;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.libdroid.util.ResultAction;
import com.github.mrm1st3r.libdroid.widget.HashMapAdapter;

/**
 * This activity will create a new Bluetooth server socket and wait for incoming
 * connections.
 */
public class LobbyCreateActivity extends Activity {

	private static final String TAG = LobbyCreateActivity.class.getSimpleName();
	private static final int BLUETOOTH_VISIBLE_SECONDS = 60;
	private static final int MAXIMUM_REMOTE_PLAYER_COUNT = 3;

	private ServerThread connectionThread = null;
	private HashMap<SimpleBluetoothConnection, String> connectedPlayers = new HashMap<>();
	private HashMapAdapter<SimpleBluetoothConnection, String> connectedPlayerAdapter;
	private Button startButton = null;
	private String localPlayerName;

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!BluetoothUtil.isSupported()) {
			Toast.makeText(this, getString(R.string.bluetooth_not_supported),
					Toast.LENGTH_LONG).show();
			onBackPressed();
		}

		setContentView(R.layout.activity_lobby_create);
		((TextView) findViewById(R.id.txtLobbyName)).setText(BluetoothUtil.getDeviceName());

		BluetoothUtil.requestEnable(this, new ResultAction() {
			@Override
			public void onSuccess() {
				BluetoothUtil.requestEnableDiscoverable(
						LobbyCreateActivity.this,
						BLUETOOTH_VISIBLE_SECONDS, new ResultAction() {
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
	}

	private void createLobby() {
		if (connectionThread != null) {
			return;
		}
		SharedPreferences pref = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);
		localPlayerName = pref.getString(Cards.PREF_PLAYER_NAME, "");
		connectedPlayers.put(null, localPlayerName);

		startButton = (Button) findViewById(R.id.btnStart);
		LobbyFragment lobbyFragment = (LobbyFragment) getFragmentManager().findFragmentById(R.id.player_list);

		connectedPlayerAdapter = new HashMapAdapter<SimpleBluetoothConnection, String>(
				LobbyCreateActivity.this, connectedPlayers) {
			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {
				TextView rowView;
				if (convertView == null) {
					LayoutInflater inflater = (LayoutInflater) getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
				} else {
					rowView = (TextView) convertView;
				}
				rowView.setText(getItem(pos));
				return rowView;
			}

		};
		lobbyFragment.setAdapter(connectedPlayerAdapter);

		connectionThread = new ServerThread(getString(R.string.app_name),
				UUID.fromString(Cards.UUID), new OnConnectionChangeHandler() {
					@Override
					public void onConnect(ThreadedConnection conn) {
						clientConnected(conn);
					}
				});
		connectionThread.start();
	}

	private void clientConnected(ThreadedConnection tc) {
		SimpleBluetoothConnection conn = (SimpleBluetoothConnection) tc;

		if (connectedPlayers.size() == MAXIMUM_REMOTE_PLAYER_COUNT) {
			conn.write("quit");
			conn.close();
			return;
		}
		for (String player : connectedPlayers.values()) {
			conn.write("join " + player);
		}
		conn.setOnReceivedHandler(new OnReceivedHandler<String>() {
			@Override
			public void onReceived(AsynchronousConnection<String> ac, String msg) {
				handleIncomingMessage(ac, msg);
			}
		});
		conn.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
			@Override
			public void onDisconnect(ThreadedConnection conn) {
				playerLeft(conn);
			}
		});
		conn.start();
	}

	private void playerLeft(ThreadedConnection conn) {
		String leftPlayer = connectedPlayers.get(conn);
		connectedPlayers.remove(conn);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connectedPlayerAdapter.notifyDataSetChanged();
				if (connectedPlayers.size() == 0) {
					startButton.setEnabled(false);
				}
			}
		});
		broadcast("left " + leftPlayer);
	}

	private void handleIncomingMessage(AsynchronousConnection<String> ac, final String msg) {
		SimpleBluetoothConnection conn = (SimpleBluetoothConnection) ac;
		String[] set = msg.split(" ");
		final int namePos = 5;
		if (set[0].equals("join")) {
			if (set[1].length() == 0) {
				return;
			}
			connectedPlayers.put(conn, msg.substring(namePos));
			broadcast(msg);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					startButton.setEnabled(true);
					Log.d(TAG, "received: " + msg);
					connectedPlayerAdapter.notifyDataSetChanged();
				}
			});
		} else if (set[0].equals("left") && set[1].equals(connectedPlayers.get(ac))) {
			playerLeft((SimpleBluetoothConnection) ac);
		} else {
			Log.w(TAG, "Illegal message received: " + msg);
		}
	}

	private void broadcast(String msg) {
		for (SimpleBluetoothConnection c : connectedPlayers.keySet()) {
			if (c != null) {
				c.write(msg);
			}
		}
	}

	public void start(View v) {
		((Cards) getApplication()).setConnections(connectedPlayers);
		connectedPlayers.remove(null);
		for (SimpleBluetoothConnection conn : connectedPlayers.keySet()) {
			conn.write("start");
			conn.pause();
		}
		connectionThread.close();
		Intent intent = new Intent(this, HostGameActivity.class);
		intent.putExtra(Cards.EXTRA_LOCAL_NAME, localPlayerName);
		startActivity(intent);
		finish();
	}

	private void cancelLobby() {
		if (connectionThread != null) {
			connectionThread.close();
		}
		connectedPlayers.remove(null);
		for (SimpleBluetoothConnection conn : connectedPlayers.keySet()) {
			conn.write("quit");
			conn.close();
		}
		connectedPlayers.clear();
	}

	public void becomeVisible(MenuItem item) {
		BluetoothUtil.requestEnableDiscoverable(this, BLUETOOTH_VISIBLE_SECONDS, null);
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		BluetoothUtil.onActivityResult(this, reqCode, resultCode, data);
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
