package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.game.ui.HostGameActivity;

/**
 * This class describes a local player.
 * @author Sergius Maier
 * @version 1.0
 */
public class LocalPlayer extends Player {

	/**
	 * The local user interface to interact with.
	 */
	private HostGameActivity userInterface;

	/**
	 * Construct a new local player.
	 * @param pName The players name
	 * @param pHandSize The maximum number of hand cards
	 * @param pLifes The number of lifes to begin with
	 * @param pUi The local user interface
	 */
	public LocalPlayer(final String pName, final int pHandSize,
			final int pLifes, final HostGameActivity pUi) {	
		super(pName, pHandSize, pLifes);
		userInterface = pUi;
	}

	/**
	 * @return The local user interface
	 */
	public final HostGameActivity getUserInterface() {
		return userInterface;
	}

	@Override
	public final void sendMessage(final String msg) {
		userInterface.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				userInterface.checkMessage(msg);
			}
		});
	}
}