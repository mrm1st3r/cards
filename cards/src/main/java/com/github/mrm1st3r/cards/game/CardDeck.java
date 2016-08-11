package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * This class contains data and functions that are needed to describe a card
 * deck.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.0
 */
public class CardDeck {

	/**
	 * All cards currently contained in this card deck.
	 */
	private LinkedList<Card> mCardDeck = new LinkedList<Card>();

	/**
	 * Maximum number of cards to be held in this card deck (-1 for no limit).
	 */
	private int mSizeLimit = -1;

	/**
	 * Get a list that contains all cards currently in this deck.
	 * 
	 * @return All table cards
	 */
	public final Card[] getAll() {
		Card[] arr = new Card[mCardDeck.size()];
		return mCardDeck.toArray(arr);
	}

	/**
	 * Get a card from this deck based on it's position.
	 * 
	 * @param pos
	 *            Card position on the table
	 * @return The card from the table
	 */
	public final Card getCard(final int pos) {
		return mCardDeck.get(pos);
	}

	/**
	 * Add a new card to this deck.
	 * 
	 * @param c
	 *            New card
	 * @return true on success, false otherwise
	 */
	public final boolean addCard(final Card c) {
		if (mSizeLimit >= 0 && mCardDeck.size() == mSizeLimit) {
			return false;
		}

		mCardDeck.add(c);
		return true;
	}

	/**
	 * Remove a card from this deck.
	 * 
	 * @param c
	 *            Card to remove
	 * @return true on success, false otherwise
	 */
	public final boolean removeCard(final Card c) {
		if (mCardDeck.contains(c)) {
			mCardDeck.remove(c);
			return true;
		}
		return false;
	}

	/**
	 * Swap a card in this deck with another.
	 * 
	 * @param take
	 *            Card to remove from this deck
	 * @param give
	 *            Card to add to this deck
	 * @return true on success, false otherwise
	 */
	public final boolean replaceCard(final Card take, final Card give) {
		if (removeCard(take)) {
			return addCard(give);
		}
		return false;
	}

	/**
	 * Remove all cards from this deck.
	 */
	public final void reset() {
		mCardDeck.clear();
	}

	/**
	 * Replace the cards from this deck with cards from another deck.
	 * 
	 * @param pNewCards
	 *            New set of cards for this deck
	 */
	public final void replaceDeck(final CardDeck pNewCards) {
		if (mSizeLimit >= 0 && pNewCards.mCardDeck.size() > mSizeLimit) {
			throw new IllegalArgumentException("Too many cards in new deck");
		}
		mCardDeck = pNewCards.mCardDeck;
	}

	/**
	 * Swap the cards of this deck with the cards from another deck.
	 * 
	 * @param pReplace
	 *            Deck to swap with
	 */
	public final void swapWith(final CardDeck pReplace) {
		if ((mSizeLimit >= 0 && pReplace.mCardDeck.size() > mSizeLimit)
				|| (pReplace.mSizeLimit >= 0
				&& mCardDeck.size() > pReplace.mSizeLimit)) {
			throw new IllegalArgumentException("Deck sizes don't match");
		}

		LinkedList<Card> temp = mCardDeck;

		mCardDeck = pReplace.mCardDeck;
		pReplace.mCardDeck = temp;
	}

	/**
	 * Get the maximum number of cards contained in this card deck.
	 * 
	 * @return Maximum number of cards in this deck
	 */
	public final int getLimit() {
		return mSizeLimit;
	}

	/**
	 * Set a new maximum number of cards in this deck.<br />
	 * To disable limit set to -1
	 * 
	 * @param pLimit
	 *            New maximum number of cards
	 */
	public final void setLimit(final int pLimit) {
		mSizeLimit = pLimit;
	}

	/**
	 * Get the image names for all contained cards.
	 * @return All card images
	 */
	public final String[] getImages() {
		String[] images = new String[mCardDeck.size()];
		
		for (int i = 0; i < mCardDeck.size(); i++) {
			images[i] = mCardDeck.get(i).getImageName();
		}
		
		return images;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();

		for (Card c : mCardDeck) {
			sb.append(c.toString());
			sb.append(' ');
		}
		sb.deleteCharAt(sb.length());

		return sb.toString();
	}
}
