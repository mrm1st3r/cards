package com.github.mrm1st3r.cards.game;


/**
 * This class describes a card player.
 * 
 * @author Lukas 'mrm1st3r' Taake, Sergius Maier
 * @version 1.1.0
 */
public abstract class Player {

	/**
	 * The players name.
	 */
	private final String mName;
	/**
	 * The players hand cards.
	 */
	private final CardDeck mHandCards;
	/**
	 * Lock used when waiting for player input.
	 */
	private final Object mLock = new Object();
	/**
	 * The number of lifes the player has left.
	 */
	private int mLifes = 0;
	/**
	 * The number of points the player has reached so far.
	 */
	private float mScore = 0;

	/**
	 * Construct a new card player.
	 * 
	 * @param pName
	 *            The players name
	 */
	public Player(final String pName) {
		mName = pName;
		mHandCards = new CardDeck();
	}

	/**
	 * Construct a new card player.
	 * 
	 * @param pName
	 *            The players name
	 * @param pHandLimit
	 *            Maximum number of hand cards
	 * @param pLifes
	 *            Number of lifes to start with
	 */
	public Player(final String pName, final int pHandLimit, final int pLifes) {
		this(pName);
		mHandCards.setLimit(pHandLimit);
	}

	/**
	 * Send a message to the player.
	 * 
	 * @param pComm
	 *            Message to send
	 */
	public abstract void command(String pComm);

	/**
	 * Get the players name.
	 * 
	 * @return The players name
	 */
	public final String getName() {
		return mName;
	}

	/**
	 * Get the players score.
	 * 
	 * @return The players current score
	 */
	public final float getScore() {
		return mScore;
	}

	/**
	 * Update the players score.
	 * 
	 * @param pScore
	 *            The new score
	 */
	public final void setScore(final float pScore) {
		mScore = pScore;
	}

	/**
	 * Get all current hand cards.
	 * 
	 * @return The players current hand cards
	 */
	public final CardDeck getHand() {
		return mHandCards;
	}

	/**
	 * Get the current number of lifes.
	 * 
	 * @return The players current lifes
	 */
	public final int getLifes() {
		return mLifes;
	}

	/**
	 * Update the players lifes.
	 * 
	 * @param pLifes
	 *            New number of lifes
	 */
	public final void setLifes(final int pLifes) {
		mLifes = pLifes;
	}

	/**
	 * Decrease the number of lifes by one.
	 */
	public final void decreaseLifes() {
		mLifes--;
	}

	/**
	 * Get the players thread lock.
	 * 
	 * @return This players lock object
	 */
	public final Object getLock() {
		return mLock;
	}

	@Override
	public final String toString() {
		return mName + " has " + mScore + " points and " + mLifes + " lifes";
	}
}
