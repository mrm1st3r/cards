package com.github.mrm1st3r.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mrm1st3r.cards.lobby.LobbyCreateActivity;
import com.github.mrm1st3r.cards.lobby.LobbyJoinActivity;

public class MainActivity extends Activity {

	public static final String PREF_PLAYER_NAME = "player_name";
	
	private EditText inputPlayerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences pref = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
		
		inputPlayerName = (EditText) findViewById(R.id.input_player_name);
		inputPlayerName.setText(pref.getString(MainActivity.PREF_PLAYER_NAME, ""));
		inputPlayerName.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable input) {
				SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.pref_file),
						Context.MODE_PRIVATE).edit();
				
				edit.putString(MainActivity.PREF_PLAYER_NAME, input.toString());
				edit.commit();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) { }

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) { }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void startGame(View v) {
		Toast.makeText(this, "clicked " + ((Button) v).getText(), Toast.LENGTH_LONG).show();
		Class<? extends Activity> activity = null;
		int id = v.getId();
		
		if (id == R.id.btn_new_game) {
			activity = LobbyCreateActivity.class;
		} else if (id == R.id.btn_join_game) {
			activity = LobbyJoinActivity.class;
		} else if (id == R.id.btn_bot_game) {
			return;
			// TODO: implement playing against computer
		} else {
			// unknown action
			return;
		}
		
		Intent intent = new Intent(this, activity);
		startActivity(intent);
	}
}
