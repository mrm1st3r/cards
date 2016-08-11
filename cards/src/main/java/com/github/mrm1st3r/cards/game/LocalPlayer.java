package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.cards.game.ui.GameActivity;

/**
 * This class describes a local player.
 * 
 * @author Lukas 'mrm1st3r' Taake, Sergius Maier
 * @version 1.0.2
 */
public class LocalPlayer extends Player {

	/**
	 * The local user interface to interact with.
	 */
	private final GameActivity mUserInterface;

	/**
	 * Construct a new local player.
	 * 
	 * @param pName
	 *            Player name
	 * @param pUi
	 *            Local user interface
	 */
	public LocalPlayer(final String pName, final GameActivity pUi) {
		super(pName);
		mUserInterface = pUi;
	}

	/**
	 * Construct a new local player.
	 * 
	 * @param pName
	 *            The players name
	 * @param pHandSize
	 *            The maximum number of hand cards
	 * @param pLifes
	 *            The number of lifes to begin with
	 * @param pUi
	 *            The local user interface
	 */
	public LocalPlayer(final String pName, final int pHandSize,
			final int pLifes, final GameActivity pUi) {
		super(pName, pHandSize, pLifes);
		mUserInterface = pUi;
	}

	/**
	 * Get the local user interface.
	 * 
	 * @return The local user interface
	 */
	public final GameActivity getUserInterface() {
		return mUserInterface;
	}

	@Override
	public final void command(final String msg) {

		// process an incoming message from the game loop on the UI thread to
		// be able to interact with it.
		mUserInterface.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mUserInterface.handleMessage(msg);
			}
		});
	}
}
