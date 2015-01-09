package com.github.mrm1st3r.cards.game;

/**
 * This class describes a playing card.
 * 
 * @author Sergius Maier
 * @version 2.0
 */
public class Card {

	/**
	 * The cards value.
	 */
	private CardValue value;
	/**
	 * The cards color.
	 */
	private CardColor color;
	/**
	 * The cards image name.
	 */
	private String imageName;

	/**
	 * Construct a new playing card.
	 * @param pColor The cards color
	 * @param pValue The cards value
	 */
	public Card(final CardColor pColor, final CardValue pValue) {
		color = pColor;
		value = pValue;
		imageName = "card_" + color + "_" + value;
	}


	/**
	 * @return The cards color
	 */
	public final CardColor getColor() {
		return color;
	}

	/**
	 * @return The cards value
	 */
	public final CardValue getValue() {
		return value;
	}

	/**
	 * @return The cards integer value
	 */
	public final int getIntValue() {
		return value.getValue();
	}

	/**
	 * @return The name of the image representing this card
	 */
	public final String getImageName() {
		return imageName;
	}


	@Override
	public final String toString() {
		return value  + " of " + color;
	}
}