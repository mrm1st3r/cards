package com.github.mrm1st3r.cards;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.github.mrm1st3r.cards.lobby.LobbyCreateActivity;
import com.github.mrm1st3r.cards.lobby.LobbyJoinActivity;

/**
 * This is the main activity that is started when the app gets started.
 * It contains a player name input field and buttons to reach all of
 * the apps functions.
 * 
 * @author Lukas 'mrm1st3r' Taake
 */
public class MainActivity extends Activity {

	/**
	 * Debug tag.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();

	/**
	 * Preferences for player name.
	 */
	private SharedPreferences pref;
	/**
	 * Input field for player name.
	 */
	private EditText inputPlayerName;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// read currently saved player name
		
		pref = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);

		// use null as default value to be able to immediately save
		// default value to prevent empty user name.
		String name = pref.getString(Cards.PREF_PLAYER_NAME, null);

		if (name == null) {
			name = BluetoothAdapter.getDefaultAdapter().getName();
			changePlayerName(name);
		}

		inputPlayerName = (EditText) findViewById(R.id.input_player_name);
		inputPlayerName.setText(name);
		inputPlayerName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(final Editable input) { }
			
			@Override
			public void beforeTextChanged(final CharSequence arg0,
					final int arg1, final int arg2, final int arg3) { }
	
			@Override
			public void onTextChanged(final CharSequence name, final int arg1,
					final int arg2,	final int arg3) {
				changePlayerName(name.toString());
			}
		});
	}

	/**
	 * Write a new player name to preferences.
	 * @param newName Player name to wrote
	 */
	private void changePlayerName(final String newName) {
		SharedPreferences.Editor edit = pref.edit();

		edit.putString(Cards.PREF_PLAYER_NAME, newName);
		edit.commit();
	}

	/**
	 * React to a user input and start a new activity.
	 * @param v The button that was pressed
	 */
	public final void onButtonPressed(final View v) {
		Class<? extends Activity> activity = null;
		int id = v.getId();
		
		if (id == R.id.btn_new_game) {
			activity = LobbyCreateActivity.class;
		} else if (id == R.id.btn_join_game) {
			activity = LobbyJoinActivity.class;
		} else if (id == R.id.btn_bot_game) {
			return;
			// TODO: implement playing against computer
		} else if (id == R.id.btn_rules) {
			activity = RulesActivity.class;
		} else {
			return;
		}
		
		Intent intent = new Intent(this, activity);
		startActivity(intent);
	}
}
