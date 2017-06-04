package com.github.mrm1st3r.cards.activity;

import java.util.LinkedList;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.libdroid.connect.OnConnectionChangeHandler;
import com.github.mrm1st3r.libdroid.connect.ThreadedConnection;
import com.github.mrm1st3r.libdroid.connect.bluetooth.BluetoothUtil;
import com.github.mrm1st3r.libdroid.connect.bluetooth.ClientThread;
import com.github.mrm1st3r.libdroid.connect.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.libdroid.util.ResultAction;

/**
 * This activity lists all currently available bluetooth devices and gives the
 * user the ability to connect to one.
 */
public class LobbyJoinActivity extends Activity {

	private static final String TAG = LobbyJoinActivity.class.getSimpleName();
	private LinkedList<BluetoothDevice> removeDevices;
	private ArrayAdapter<BluetoothDevice> removeDevicesAdapter;
	private ClientThread connectorThread = null;
	private Button listRefreshButton = null;
	private ProgressDialog joinDialog = null;

	private final BroadcastReceiver connectionEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				listRefreshButton.setEnabled(true);
				listRefreshButton.setText(getResources().getString(R.string.refresh));

			} else if (!BluetoothDevice.ACTION_FOUND.equals(action)) {
				// theoretically there should be no other broadcasts
				// received here. Just in case.
				return;
			}
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (device == null) {
				return;
			}
			Log.d(TAG, String.format("Found new device: %s / %s", device.getAddress(), device.getName()));
			removeDevices.add(device);
			removeDevicesAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_join);
		if (!BluetoothUtil.isSupported()) {
			Toast.makeText(this, getString(R.string.bluetooth_not_supported),
					Toast.LENGTH_LONG).show();
			cancelSearch();
		}
		BluetoothUtil.requestEnable(this, new ResultAction() {
			@Override
			public void onSuccess() {
				initUi();
				searchForLobbies();
			}
			@Override
			public void onFailure() {
				onBackPressed();
			}
		});
	}

	private void initUi() {
		listRefreshButton = (Button) findViewById(R.id.btnRefresh);
		ListView mDeviceListView = (ListView) findViewById(R.id.lobbyList);
		removeDevices = new LinkedList<>();
		removeDevicesAdapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_1, removeDevices) {
			@NonNull
			@Override
			public View getView(int pos, View vOld, @NonNull ViewGroup parent) {
				View vNew = super.getView(pos, vOld, parent);
				((TextView) vNew).setText(getItem(pos).getName());
				return vNew;
			}
		};
		mDeviceListView.setAdapter(removeDevicesAdapter);
		mDeviceListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				joinLobby(removeDevicesAdapter.getItem(pos));
			}
		});
	}

	private void searchForLobbies() {
		Log.d(TAG, "searching for new devices...");
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(connectionEventReceiver, filter);
		refresh(null);
	}

	private void cancelSearch() {
		Log.d(TAG, "canceling search process");
		try {
			unregisterReceiver(connectionEventReceiver);
		} catch (IllegalArgumentException ignored) {}
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}

	public void refresh(View v) {
		BluetoothAdapter self = BluetoothAdapter.getDefaultAdapter();
		self.cancelDiscovery();
		removeDevices.clear();
		removeDevicesAdapter.notifyDataSetChanged();
		if (!self.startDiscovery()) {
			Toast.makeText(this, getString(R.string.refresh_failed),
					Toast.LENGTH_SHORT).show();
			Log.w(TAG, "Failed to start bluetooth discovery");
		} else {
			listRefreshButton.setEnabled(false);
			listRefreshButton.setText(getResources().getString(R.string.refreshing));
		}
	}

	private void joinLobby(BluetoothDevice dev) {
		Log.d(TAG, "connecting to " + dev.getAddress());
		joinDialog = new ProgressDialog(this);
		joinDialog.setCancelable(true);
		joinDialog.setCanceledOnTouchOutside(false);
		joinDialog.setMessage(getResources().getString(R.string.joining));
		joinDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getResources()
				.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						connectorThread.close();
					}
				});
		joinDialog.show();

		connectorThread = new ClientThread(dev, UUID.fromString(Cards.UUID));

		connectorThread.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
			@Override
			public void onConnect(ThreadedConnection tc) {
				SimpleBluetoothConnection conn = (SimpleBluetoothConnection) tc;
				Log.d(TAG, "joining lobby " + conn.getDeviceName());
				((Cards) getApplication()).getConnections().clear();
				((Cards) getApplication()).getConnections().put(conn, null);
				cancelSearch();
				joinDialog.dismiss();
				Intent intent = new Intent(LobbyJoinActivity.this, LobbyActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				finish();
			}

			@Override
			public void onConnectionFailed(BluetoothDevice dev) {
				joinDialog.dismiss();
				removeDevices.remove(dev);
				Log.w(TAG, "failed to connect to " + dev.getName());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								LobbyJoinActivity.this,
								getResources().getString(
										R.string.connection_failed),
								Toast.LENGTH_SHORT).show();
						removeDevicesAdapter.notifyDataSetChanged();
					}
				});
			}
		});
		connectorThread.start();
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		BluetoothUtil.onActivityResult(this, reqCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		cancelSearch();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (removeDevicesAdapter != null) {
			removeDevicesAdapter.notifyDataSetChanged();
		}
	}
}
