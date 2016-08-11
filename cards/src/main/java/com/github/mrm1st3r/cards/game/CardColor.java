package com.github.mrm1st3r.cards.game;

import java.util.Locale;

/**
 * This enumeration contains all four colors used
 * in a standard French style card deck.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.1
 */
public enum CardColor {

	/**
	 * Spades.
	 */
	SPADES,
	/**
	 * Clubs.
	 */
	CLUBS,
	/**
	 * Hearts.
	 */
	HEARTS,
	/**
	 * Diamonds.
	 */
	DIAMONDS;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
