package com.github.mrm1st3r.cards.game;

/**
 * All possible four game card colors.
 * 
 * @author Lukas 'mrm1st3r' Taake, Sergius Maier
 * @version 1.0
 */
public enum CardColor {

	/**
	 * Spades.
	 */
	SPADES("spades"),
	/**
	 * Clubs.
	 */
	CLUBS("clubs"),
	/**
	 * Hearts.
	 */
	HEARTS("hearts"),
	/**
	 * Diamonds.
	 */
	DIAMONDS("diamonds");

	/**
	 * The card color name.
	 */
	private String name;

	/**
	 * Construct a new card color.
	 * 
	 * @param colorName
	 *            The colors name
	 */
	private CardColor(final String colorName) {
		name = colorName;
	}

	/**
	 * @return The colors name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
