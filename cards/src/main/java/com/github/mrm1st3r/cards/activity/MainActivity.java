package com.github.mrm1st3r.cards.activity;

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

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.Constant;
import com.github.mrm1st3r.cards.R;

/**
 * This is the main activity that is started when the app gets started. It
 * contains a player name input field and buttons to reach all of the apps
 * functions.
 *
 * @author Lukas Taake
 */
public class MainActivity extends Activity {

	private SharedPreferences prefs;

	private EditText playerNameInput;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = getSharedPreferences(Cards.PREF_FILE, Context.MODE_PRIVATE);
		String name = prefs.getString(Cards.PREF_PLAYER_NAME, null);

		if (name == null) {
			name = getDefaultPlayerName();
			changePlayerName(name);
		}

		setupNameInput(name);
	}

	private String getDefaultPlayerName() {
		return BluetoothAdapter.getDefaultAdapter().getName();
	}

	private void setupNameInput(String name) {
		playerNameInput = (EditText) findViewById(R.id.input_player_name);
		playerNameInput.setText(name);
		playerNameInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(final Editable input) {
			}

			@Override
			public void beforeTextChanged(final CharSequence arg0,
					final int arg1, final int arg2, final int arg3) {
			}

			@Override
			public void onTextChanged(final CharSequence name, final int arg1,
					final int arg2, final int arg3) {
				changePlayerName(name.toString());
			}
		});
	}

	private void changePlayerName(final String newName) {
		SharedPreferences.Editor edit = prefs.edit();

		edit.putString(Cards.PREF_PLAYER_NAME, newName);
		edit.commit();
	}

	public final void onButtonPressed(final View button) {
		Class<? extends Activity> activity;
		int buttonId = button.getId();

		switch(buttonId) {
			case R.id.btn_new_game:
				activity = LobbyCreateActivity.class;
				break;
			case R.id.btn_join_game:
				activity = LobbyJoinActivity.class;
				break;
			case R.id.btn_bot_game:
				activity = BotGameActivity.class;
				break;
			case R.id.btn_rules:
				activity = RulesActivity.class;
				break;
			default:
				return;
		}
		Intent intent = new Intent(this, activity);
		intent.putExtra(Constant.EXTRA_LOCAL_NAME, playerNameInput.getText());
		startActivity(intent);
	}
}
