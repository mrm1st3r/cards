package com.github.mrm1st3r.cards.game;

/**
 * This class describes a playing card.
 * 
 * @author Lukas 'mrm1st3r' Taake, Sergius Maier
 * @version 1.0.0
 */
public class Card {

	/**
	 * The cards value.
	 */
	private final CardValue mValue;
	/**
	 * The cards color.
	 */
	private final CardColor mColor;
	/**
	 * The cards image name.
	 */
	private final String mImageName;

	/**
	 * Construct a new playing card.
	 * 
	 * @param pColor
	 *            The cards color
	 * @param pValue
	 *            The cards value
	 */
	public Card(final CardColor pColor, final CardValue pValue) {
		mColor = pColor;
		mValue = pValue;
		mImageName = "card_" + mColor + "_" + mValue;
	}

	/**
	 * Get the cards color.
	 * @return The cards color
	 */
	public final CardColor getColor() {
		return mColor;
	}

	/**
	 * Get the cards value.
	 * @return The cards value
	 */
	public final CardValue getValue() {
		return mValue;
	}

	/**
	 * Get the cards integer value.
	 * Will return the same value as {@link CardValue#getValue()}.
	 * @return The cards integer value
	 */
	public final int getIntValue() {
		return mValue.getValue();
	}

	/**
	 * Get the name of the image representing this card.
	 * @return The name of the image representing this card
	 */
	public final String getImageName() {
		return mImageName;
	}

	@Override
	public final String toString() {
		return mValue + " of " + mColor;
	}
}
