package com.github.mrm1st3r.cards.lobby;

import java.util.HashMap;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;
import com.github.mrm1st3r.btutil.BluetoothConnection;
import com.github.mrm1st3r.btutil.BtUtil;
import com.github.mrm1st3r.btutil.OnConnectHandler;
import com.github.mrm1st3r.btutil.ResultAction;
import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.connection.ClientThread;
import com.github.mrm1st3r.util.HashMapAdapter;

public class LobbyJoinActivity extends Activity {

	private static final String TAG = LobbyJoinActivity.class.getSimpleName();
	private HashMapAdapter<String, BluetoothDevice> mDeviceAdapter;
	private HashMap<String, BluetoothDevice> mDeviceList;
	private ListView deviceList;
	private ClientThread conn = null;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			//Log.d(TAG, "Received Broadcast of type " + action);
			
			if (!BluetoothDevice.ACTION_FOUND.equals(action)) {
				return;
			}
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (device == null) {
				return;
			}
			Log.d(TAG, device.getName() + " / " + device.getAddress());
			// Add the name and address to an array adapter to show in a ListView
			mDeviceList.put(device.getAddress(), device);
			mDeviceAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_join);

		// go back to start if bluetooth is not supported
		if (!BtUtil.isSupported()) {
			Toast.makeText(this, getString(
					R.string.bluetooth_not_supported), Toast.LENGTH_LONG)
					.show();
			cancel();
		}
		
		BtUtil.enable(this, new ResultAction() {

			@Override
			public void onSuccess() {
				searchForLobbies();
			}

			@Override
			public void onFailure() {
				cancel();
			}

		});
		
		deviceList = (ListView)findViewById(R.id.lobbyList);
	}

	protected final void onDestroy()  {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}
	
	private void cancel() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby_join, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {

		if (BtUtil.onActivityResult(this, reqCode, resultCode, data)) {
			// Bluetooth results should be covered by BtUtil.
			return;
		}

	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void searchForLobbies() {
		Log.d(TAG, "searching for new devices...");
		mDeviceList = new HashMap<String, BluetoothDevice>();

		mDeviceAdapter = new HashMapAdapter<String, BluetoothDevice>(this,
				mDeviceList) {

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
						//TextView keyView = (TextView) rowView.findViewById(R.id.item_name);
						
						rowView.setText(getItem(pos).getName());

						return rowView;
					}
			
		};
		deviceList.setAdapter(mDeviceAdapter);
		
		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				joinLobby(mDeviceAdapter.getItem(arg2));
			}
			
		});
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		
		refresh(null);
	}
	

	public void refresh(View v) {
		BluetoothAdapter self = BluetoothAdapter.getDefaultAdapter();
		self.cancelDiscovery();
		mDeviceList.clear();
		if (!self.startDiscovery()) {
			Toast.makeText(this, "Fehler beim aktualisieren", Toast.LENGTH_SHORT).show();
			Log.w(TAG, "Failed to start bluetooth discovery");
		}
	}

	private void joinLobby(BluetoothDevice dev) {
		Log.d(TAG, "connecting to " + dev.getAddress());
		conn = new ClientThread(this, dev, new OnConnectHandler() {

			@Override
			public void onConnect(BluetoothConnection conn) {
				SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
				
				String name = pref.getString(MainActivity.PREF_PLAYER_NAME, "");
				AsyncBluetoothConnection asConn = (AsyncBluetoothConnection) conn;
				asConn.write(name);
				asConn.pause();
				((Cards)getApplication()).connections.put(asConn, null);
				Intent intent = new Intent(LobbyJoinActivity.this, LobbyActivity.class);

				startActivity(intent);
			}
		});
		conn.start();
	}
}
