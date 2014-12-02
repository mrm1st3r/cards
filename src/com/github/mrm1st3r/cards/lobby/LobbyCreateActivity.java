package com.github.mrm1st3r.cards.lobby;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.connection.ServerThread;

public class LobbyCreateActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_ENABLE_DISCOVERABLE = 2;
	
	private static final int LOBBY_CREATE_TIMEOUT = 60;

	private ServerThread serv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BluetoothAdapter mBluetoothAdapter =
				BluetoothAdapter.getDefaultAdapter();
		// bluetooth not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, getString(
					R.string.bluetooth_not_supported), Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			enableDiscoverability();
		}

		setContentView(R.layout.activity_lobby_create);
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		if (reqCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
			enableDiscoverability();
		} else if (reqCode == REQUEST_ENABLE_DISCOVERABLE
				&& resultCode == RESULT_OK) {
			this.serv = new ServerThread();
		}
	}

	private void enableDiscoverability() {
		Intent discoverableIntent =
				new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, LOBBY_CREATE_TIMEOUT);
		startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby_create, menu);
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
