package com.github.mrm1st3r.cards.game;

import java.util.Locale;

/**
 * This enumeration contains all four colors used in a standard French style card deck.
 */
enum CardColor {
	SPADES,
	CLUBS,
	HEARTS,
	DIAMONDS;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
