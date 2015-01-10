package com.github.mrm1st3r.cards.game;


/**
 * This class describes a card player.
 * @author Sergius Maier
 * @version 1.0
 */
public abstract class Player {

	/**
	 * The players name.
	 */
	private String name;
	/**
	 * The number of lifes the player has left.
	 */
	private int lifes = 0;
	/**
	 * The number of points the player has reached so far.
	 */
	private float score = 0;
	/**
	 * The players hand cards.
	 */
	private Card[] handCards;
	/**
	 * Lock used when waiting for player input.
	 */
	private Object lock = new Object();

	/**
	 * Construct a new player.
	 * @param pName The players name
	 * @param pHandSize	Maximum number of hand cards
	 * @param pLifes Number of lifes to start with
	 */
	public Player(final String pName, final int pHandSize, final int pLifes) {
		setName(pName);
		setLifes(pLifes);
		setHand(new Card[pHandSize]);
	}

	/**
	 * Add a new card to the players hand.
	 * @param c	New hand card
	 * @return	true on success, false otherwise
	 */
	public final boolean addToHand(final Card c) {
		for (int i = 0; i < handCards.length; i++) {
			if (handCards[i] == null) {
				handCards[i] = c;
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove a card from the players hand.
	 * @param c Card to remove
	 * @return true on success, false otherwise
	 */
	public final boolean removeFromHand(final Card c) {
		for (int i = 0; i < handCards.length; i++) {
			if (handCards[i].equals(c)) {
				handCards[i] = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * Send a message to the player.
	 * @param msg Message to send
	 */
	public abstract void sendMessage(String msg);

	/**
	 * @return The players name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Change the players name.
	 * @param pName New player name
	 */
	public final void setName(final String pName) {
		this.name = pName;
	}

	/**
	 * @return The players current score
	 */
	public final float getScore() {
		return score;
	}

	/**
	 * Update the players score.
	 * @param pScore The new score
	 */
	public final void setScore(final float pScore) {
		this.score = pScore;
	}

	/**
	 * @return The players current hand cards
	 */
	public final Card[] getHand() {
		return handCards;
	}
	
	/**
	 * Get a specified hand card.
	 * @param pos Position of the request card in the players hand
	 * @return The requested card
	 */
	public final Card getHandCard(final int pos) {
		return handCards[pos];
	}

	/**
	 * Give the player new hand cards.
	 * @param hand New hand cards
	 */
	public final void setHand(final Card[] hand) {
		this.handCards = hand;
	}

	/**
	 * @return The players current lifes
	 */
	public final int getLifes() {
		return lifes;
	}

	/**
	 * Update the players lifes.
	 * @param pLifes New number of lifes
	 */
	public final void setLifes(final int pLifes) {
		lifes = pLifes;
	}

	/**
	 * Decrease the number of lifes by one.
	 */
	public final void decreaseLife() {
		lifes--;
	}

	/**
	 * @return This players lock object
	 */
	public final Object getLock() {
		return lock;
	}

	@Override
	public final String toString() {
		return name + " has " + score + " points and " + lifes + " lifes";
	}
}