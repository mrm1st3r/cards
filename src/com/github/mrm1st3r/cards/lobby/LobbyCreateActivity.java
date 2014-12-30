package com.github.mrm1st3r.cards.lobby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrm1st3r.btutil.BtUtil;
import com.github.mrm1st3r.btutil.ResultAction;
import com.github.mrm1st3r.cards.MainActivity;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.connection.ServerThread;

public class LobbyCreateActivity extends Activity {

	private static final int LOBBY_CREATE_TIMEOUT = 60;

	private ServerThread serv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// go back to start if bluetooth is not supported
		if (!BtUtil.isSupported()) {
			Toast.makeText(this, getString(
					R.string.bluetooth_not_supported), Toast.LENGTH_LONG)
					.show();
			cancelLobby();
		}

		BtUtil.enable(this, new ResultAction() {

			@Override
			public void onSuccess() {
				BtUtil.enableDiscoverability(LobbyCreateActivity.this,
						LOBBY_CREATE_TIMEOUT, new ResultAction() {

					@Override
					public void onSuccess() {
						serv = new ServerThread(LobbyCreateActivity.this);
						serv.start();

					}

					@Override
					public void onFailure() {
						cancelLobby();
					}

				});
			}

			@Override
			public void onFailure() {
				cancelLobby();
			}

		});

		setContentView(R.layout.activity_lobby_create);
		((TextView)findViewById(R.id.txtLobbyName)).setText(BtUtil.getDeviceName());
	}

	private void cancelLobby() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (serv != null) {
			serv.cancel();
		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {

		if (BtUtil.onActivityResult(this, reqCode, resultCode, data)) {
			// Bluetooth results should be covered by BtUtil.
			return;
		}

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
