package com.github.mrm1st3r.cards.game;

/**
 * All possible game card values.
 * @author Lukas 'mrm1st3r' Taake
 * @author Sergius Maier
 * @version 1.0
 */
public enum CardValue {

	/**
	 * Ace.
	 */
	ACE		("ace", 11),
	/**
	 * King.
	 */
	KING	("king", 10),
	/**
	 * Queen.
	 */
	QUEEN	("queen", 10),
	/**
	 * Jack.
	 */
	JACK	("jack", 10),
	/**
	 * 10.
	 */
	TEN		("10", 10),
	/**
	 * 9.
	 */
	NINE	("9", 9),
	/**
	 * 8.
	 */
	EIGHT	("8", 8),
	/**
	 * 7.
	 */
	SEVEN	("7", 7),
	/**
	 * 6.
	 */
	SIX		("6", 6),
	/**
	 * 5.
	 */
	FIVE	("5", 5),
	/**
	 * 4.
	 */
	FOUR	("4", 4),
	/**
	 * 3.
	 */
	THREE	("3", 3),
	/**
	 * 2.
	 */
	TWO		("2", 2);

	/**
	 * The number of card values contained in a poker deck.
	 */
	public static final int POKER_DECK_SIZE = 13;
	/**
	 * The number of card values contained in a skat deck.
	 */
	public static final int SKAT_DECK_SIZE = 8;
	/**
	 * The name of the card value.
	 */
	private String name;
	/**
	 * The represented numerical value.
	 */
	private int value;
	
	/**
	 * Construct a card value.
	 * @param pName The card values name
	 * @param pValue The numerical value
	 */
	private CardValue(final String pName, final int pValue) {
		name = pName;
		value = pValue;
	}

	/**
	 * @return The name of the card value.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The proper numerical value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return All card values that will be used in a skat deck.
	 */
	public static CardValue[] getSkatDeck() {
		CardValue[] skatDeck = new CardValue[SKAT_DECK_SIZE];
		CardValue[] all = values();
		
		// minimum value that is used in a skat deck.
		final int minValue = 7;
		
		for (int i = 0, j = 0; i < all.length; i++) {
			if (all[i].value >= minValue) {
				skatDeck[j++] = all[i];
			}
		}
		
		return skatDeck;
	}

	/**
	 * @return All card values that will be used in a poker deck.
	 */
	public static CardValue[] getPokerDeck() {
		return CardValue.values();
	}

	@Override
	public String toString() {
		return name;
	}
}