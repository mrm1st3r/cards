package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * This class contains data and functions that are needed to describe a card deck.
 */
class CardDeck {

	private LinkedList<Card> cards = new LinkedList<>();

	private int sizeLimit = -1;

	public Card[] getAll() {
		Card[] arr = new Card[cards.size()];
		return cards.toArray(arr);
	}

	Card getCard(int pos) {
		return cards.get(pos);
	}

	boolean addCard(Card c) {
		if (sizeLimit >= 0 && cards.size() == sizeLimit) {
			return false;
		}
		cards.add(c);
		return true;
	}

	private boolean removeCard(Card c) {
		if (cards.contains(c)) {
			cards.remove(c);
			return true;
		}
		return false;
	}

	boolean replaceCard(Card take, Card give) {
		return removeCard(take) && addCard(give);
	}

	void reset() {
		cards.clear();
	}

	void replaceDeck(CardDeck pNewCards) {
		if (sizeLimit >= 0 && pNewCards.cards.size() > sizeLimit) {
			throw new IllegalArgumentException("Too many cards in new deck");
		}
		cards = pNewCards.cards;
	}

 	void swapWith(CardDeck pReplace) {
		if ((sizeLimit >= 0 && pReplace.cards.size() > sizeLimit)
				|| (pReplace.sizeLimit >= 0 && cards.size() > pReplace.sizeLimit)) {
			throw new IllegalArgumentException("Deck sizes don't match");
		}
		LinkedList<Card> temp = cards;
		cards = pReplace.cards;
		pReplace.cards = temp;
	}

	void setLimit(int pLimit) {
		sizeLimit = pLimit;
	}

	String[] getImages() {
		String[] images = new String[cards.size()];
		for (int i = 0; i < cards.size(); i++) {
			images[i] = cards.get(i).getImageName();
		}
		return images;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Card c : cards) {
			sb.append(c.toString());
			sb.append(' ');
		}
		sb.deleteCharAt(sb.length());
		return sb.toString();
	}
}
