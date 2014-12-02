package com.github.mrm1st3r.cards.lobby;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.R.id;
import com.github.mrm1st3r.cards.R.layout;
import com.github.mrm1st3r.cards.R.menu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LobbyJoinActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby_join);

		searchForLobbies();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby_join, menu);
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

	private void searchForLobbies() {
		BluetoothAdapter self = BluetoothAdapter.getDefaultAdapter();

		self.startDiscovery();
	}
}
