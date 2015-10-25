package com.github.mrm1st3r.cards.game;

/**
 * This class represents a computer controlled game player.
 * Implements Runnable to perform logic in an extra thread.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public class ComputerPlayer extends Player implements Runnable {
	
	/**
	 * Time to wait until an action is made.
	 */
	private static final int WAIT_TIME = 2000;
	
	/**
	 * The game object.
	 */
	private CardGame mGame;
	/**
	 * The last received message.
	 */
	private String mLastMessage;

	/**
	 * Construct a new computer player.
	 * @param pName Player name
	 * @param pHandSize Number of hand cards
	 * @param pLifes Number of lifes to start with
	 */
	public ComputerPlayer(final String pName,
			final int pHandSize, final int pLifes) {
		super(pName, pHandSize, pLifes);
	}
	/**
	 * Register the game instance to use.
	 * @param pGame Game instance
	 */
	public final void setGame(final CardGame pGame) {
		mGame = pGame;
	}
	
	@Override
	public final void run() {
		try {
			Thread.sleep(WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (mLastMessage.equals("active")) {
			mGame.handleMessage(this, "push");
		}
	}

	@Override
	public final void command(final String msg) {
		mLastMessage = msg;
		
		Thread t = new Thread(this);
		t.start();
	}
}
