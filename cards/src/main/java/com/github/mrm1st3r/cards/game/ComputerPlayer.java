package com.github.mrm1st3r.cards.game;

/**
 * This class represents a computer controlled game player.
 * Implements Runnable to perform logic in an extra thread.
 */
public class ComputerPlayer extends Player implements Runnable {

	private static final int WAIT_TIME = 2000;

	private CardGame mGame;
	private String lastReceivedMessage;

	public ComputerPlayer(String pName, int pHandSize, int pLifes) {
		super(pName, pHandSize);
	}

	public void setGame(CardGame pGame) {
		mGame = pGame;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (lastReceivedMessage.equals("active")) {
			mGame.handleMessage(this, "push");
		}
	}

	@Override
	public void command(String msg) {
		lastReceivedMessage = msg;
		Thread t = new Thread(this);
		t.start();
	}
}
