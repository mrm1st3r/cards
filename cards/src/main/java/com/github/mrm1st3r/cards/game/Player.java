package com.github.mrm1st3r.cards.game;


/**
 * This class describes a card player.
 */
abstract class Player {

	private final String mName;
	private final CardDeck mHandCards;
	private final Object inputLock = new Object();
	private int numberOfLifesLeft = 0;
	private float score = 0;

	Player(String pName) {
		mName = pName;
		mHandCards = new CardDeck();
	}

	Player(String pName, int pHandLimit) {
		this(pName);
		mHandCards.setLimit(pHandLimit);
	}

	/**
	 * Send a message to the player.
	 */
	public abstract void command(String pComm);

	public String getName() {
		return mName;
	}

	float getScore() {
		return score;
	}

	void setScore(float pScore) {
		score = pScore;
	}

	CardDeck getHand() {
		return mHandCards;
	}

	int getLifesLeft() {
		return numberOfLifesLeft;
	}

	void decreaseLifes() {
		numberOfLifesLeft--;
	}

	Object getLock() {
		return inputLock;
	}

	@Override
	public String toString() {
		return mName + " has " + score + " points and " + numberOfLifesLeft + " lifes";
	}
}
