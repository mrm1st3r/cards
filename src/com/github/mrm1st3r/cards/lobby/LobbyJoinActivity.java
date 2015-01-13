package com.github.mrm1st3r.cards.lobby;

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
import com.github.mrm1st3r.connection.OnConnectionChangeHandler;
import com.github.mrm1st3r.connection.ThreadedConnection;
import com.github.mrm1st3r.connection.bluetooth.BluetoothUtil;
import com.github.mrm1st3r.connection.bluetooth.ClientThread;
import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;
import com.github.mrm1st3r.util.ResultAction;

/**
 * This activity lists all currently available bluetooth devices and gives the
 * user the ability to connect to one.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public class LobbyJoinActivity extends Activity {

	/**
	 * Debug tag.
	 */
	private static final String TAG = LobbyJoinActivity.class.getSimpleName();
	/**
	 * Adapter for remote device list.
	 */
	private ArrayAdapter<BluetoothDevice> mDeviceAdapter;
	/**
	 * List holding all available remote devices.
	 */
	private LinkedList<BluetoothDevice> mDeviceList;
	/**
	 * List that views all available remote devices.
	 */
	private ListView deviceList;
	/**
	 * Worker thread that will try to establish a connection to a selected
	 * remote device.
	 */
	private ClientThread connector = null;
	/**
	 * Button to reactivate Bluetooth discovery and to show current discovery
	 * status.
	 */
	private Button btnRefresh = null;
	/**
	 * Dialog that is shown while a new connection is being established.
	 */
	private ProgressDialog dlgJoin = null;
	/**
	 * The Bluetooth adapters enabling status before the app was started.
	 */
	private boolean oldBtState = false;

	/**
	 * Receiver that will receive any discovery results and status changes.
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				btnRefresh.setEnabled(true);
				btnRefresh.setText(getResources().getString(R.string.refresh));

			} else if (!BluetoothDevice.ACTION_FOUND.equals(action)) {
				// theoretically there should be no other broadcasts
				// received here. Just in case.
				return;
			}

			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			if (device == null) {
				return;
			}
			Log.d(TAG, "Found new device: " + device.getAddress() + " / "
					+ device.getName());

			mDeviceList.add(device);
			mDeviceAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_join);

		// go back to start if bluetooth is not supported
		if (!BluetoothUtil.isSupported()) {
			Toast.makeText(this, getString(R.string.bluetooth_not_supported),
					Toast.LENGTH_LONG).show();
			cancelSearch();
		}

		oldBtState = BluetoothUtil.isEnabled();
		BluetoothUtil.requestEnable(this, new ResultAction() {

			@Override
			public void onSuccess() {
				// user interface won't be needed if bluetooth isn't enabled
				initUi();
				searchForLobbies();
			}

			@Override
			public void onFailure() {
				onBackPressed();
			}
		});
	}

	/**
	 * Initialize user interface components.
	 */
	private void initUi() {
		btnRefresh = (Button) findViewById(R.id.btnRefresh);
		deviceList = (ListView) findViewById(R.id.lobbyList);

		mDeviceList = new LinkedList<BluetoothDevice>();

		// override ArrayAdapter with custom getView() method
		// to set BluetoothDevice.getName() as text
		mDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_1, mDeviceList) {

			@Override
			public View getView(final int pos, final View vOld,
					final ViewGroup parent) {

				View vNew = super.getView(pos, vOld, parent);
				((TextView) vNew).setText(getItem(pos).getName());
				return vNew;
			}
		};
		deviceList.setAdapter(mDeviceAdapter);

		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int pos, final long id) {
				joinLobby(mDeviceAdapter.getItem(pos));
			}

		});
		
		((Cards) getApplication()).setEnabled(!oldBtState);
	}

	/**
	 * Register {@link #mReceiver} and start a new Bluetooth discovery.
	 */
	private void searchForLobbies() {
		Log.d(TAG, "searching for new devices...");

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);

		refresh(null);
	}

	/**
	 * Cancel the current search process.
	 */
	private void cancelSearch() {
		Log.d(TAG, "canceling search process");
		try {
			unregisterReceiver(mReceiver);
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "");
		}
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}

	/**
	 * Reactivate Bluetooth discovery.
	 * 
	 * @param v
	 *            Button that was pressed
	 */
	public final void refresh(final View v) {
		BluetoothAdapter self = BluetoothAdapter.getDefaultAdapter();
		self.cancelDiscovery();

		mDeviceList.clear();
		mDeviceAdapter.notifyDataSetChanged();

		if (!self.startDiscovery()) {
			Toast.makeText(this, getString(R.string.refresh_failed),
					Toast.LENGTH_SHORT).show();
			Log.w(TAG, "Failed to start bluetooth discovery");
		} else {
			btnRefresh.setEnabled(false);
			btnRefresh.setText(getResources().getString(R.string.refreshing));
		}
	}

	/**
	 * Try to connect to a discovered remote device.
	 * 
	 * @param dev
	 *            Remote device to connect to
	 */
	private void joinLobby(final BluetoothDevice dev) {
		Log.d(TAG, "connecting to " + dev.getAddress());

		dlgJoin = new ProgressDialog(this);
		dlgJoin.setCancelable(true);
		dlgJoin.setCanceledOnTouchOutside(false);
		dlgJoin.setMessage(getResources().getString(R.string.joining));
		dlgJoin.setButton(ProgressDialog.BUTTON_NEGATIVE, getResources()
				.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						connector.close();
					}
				});
		dlgJoin.show();

		connector = new ClientThread(dev, UUID.fromString(Cards.UUID));

		connector.setOnConnectionChangeHandler(new OnConnectionChangeHandler() {
			@Override
			public void onConnect(final ThreadedConnection tc) {
				SimpleBluetoothConnection conn = (SimpleBluetoothConnection) tc;

				Log.d(TAG, "joining lobby " + conn.getDeviceName());

				((Cards) getApplication()).getConnections().clear();
				((Cards) getApplication()).getConnections().put(conn, null);
				cancelSearch();

				dlgJoin.dismiss();

				Intent intent = new Intent(LobbyJoinActivity.this,
						LobbyActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				finish();
			}

			@Override
			public void onConnectionFailed(final BluetoothDevice dev) {
				dlgJoin.dismiss();
				mDeviceList.remove(dev.getAddress());
				Log.w(TAG, "failed to connect to " + dev.getName());

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								LobbyJoinActivity.this,
								getResources().getString(
										R.string.connection_failed),
								Toast.LENGTH_SHORT).show();
						mDeviceAdapter.notifyDataSetChanged();
					}
				});

			}
		});
		connector.start();
	}

	@Override
	protected final void onActivityResult(final int reqCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (BluetoothUtil.onActivityResult(this, reqCode, resultCode, data)) {
			// Bluetooth results should be covered by BtUtil.
			return;
		}

	}

	@Override
	public final void onBackPressed() {
		super.onBackPressed();
		cancelSearch();
		finish();
	}

	@Override
	protected final void onResume() {
		super.onResume();
		if (mDeviceAdapter != null) {
			mDeviceAdapter.notifyDataSetChanged();
		}
	}
}
