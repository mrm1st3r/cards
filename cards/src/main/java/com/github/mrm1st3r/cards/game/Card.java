package com.github.mrm1st3r.cards.game;

/**
 * This class describes a playing card.
 */
class Card {

	private final CardValue mValue;
	private final CardColor mColor;
	private final String mImageName;

	Card(CardColor pColor, CardValue pValue) {
		mColor = pColor;
		mValue = pValue;
		mImageName = "card_" + mColor + "_" + mValue;
	}

	CardColor getColor() {
		return mColor;
	}

	CardValue getValue() {
		return mValue;
	}

	int getIntValue() {
		return mValue.getValue();
	}

	String getImageName() {
		return mImageName;
	}

	@Override
	public String toString() {
		return mValue + " of " + mColor;
	}
}
