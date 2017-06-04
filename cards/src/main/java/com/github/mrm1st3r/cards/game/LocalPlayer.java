package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.activity.GameActivity;

/**
 * This class describes a local player.
 */
public class LocalPlayer extends Player {

	private final GameActivity mUserInterface;

	public LocalPlayer(String pName, GameActivity pUi) {
		super(pName);
		mUserInterface = pUi;
	}

	public LocalPlayer(String pName, int pHandSize, int pLifes, GameActivity pUi) {
		super(pName, pHandSize);
		mUserInterface = pUi;
	}

	@Override
	public final void command(final String msg) {
		mUserInterface.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUserInterface.handleMessage(msg);
			}
		});
	}
}
