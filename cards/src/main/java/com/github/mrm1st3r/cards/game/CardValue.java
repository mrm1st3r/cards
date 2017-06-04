package com.github.mrm1st3r.cards.game;

/**
 * This enumeration contains all card values used
 * in a standard skat or power deck.
 */
enum CardValue {

	ACE("ace", 11),
	KING("king", 10),
	QUEEN("queen", 10),
	JACK("jack", 10),
	TEN("10", 10),
	NINE("9", 9),
	EIGHT("8", 8),
	SEVEN("7", 7),
	SIX("6", 6),
	FIVE("5", 5),
	FOUR("4", 4),
	THREE("3", 3),
	TWO("2", 2);

	public static final int POKER_DECK_SIZE = 13;
	public static final int SKAT_DECK_SIZE = 8;

	private final String mName;
	private final int mValue;

	CardValue(String name, int value) {
		mName = name;
		mValue = value;
	}

	public String getName() {
		return mName;
	}

	public int getValue() {
		return mValue;
	}

	/**
	 * Get all values that are used in a skat deck.
	 */
	public static CardValue[] getSkatDeck() {
		CardValue[] skatDeck = new CardValue[SKAT_DECK_SIZE];
		CardValue[] all = values();
		final int minValue = 7;
		int j = 0;
		for (CardValue anAll : all) {
			if (anAll.mValue >= minValue) {
				skatDeck[j++] = anAll;
			}
		}
		return skatDeck;
	}

	/**
	 * Get all values that are used in a poker deck.
	 */
	public static CardValue[] getPokerDeck() {
		return CardValue.values();
	}

	@Override
	public String toString() {
		return mName;
	}
}
