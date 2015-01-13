package com.github.mrm1st3r.cards;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity that shows the game rules.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public class RulesActivity extends Activity {

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
	}
}
