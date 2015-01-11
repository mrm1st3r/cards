package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * This abstract class describes the general functions of a card game.
 * @author Sergius Maier
 * @version 1.0
 */
public abstract class Gameplay {

	/**
	 * The maximum number of players in this game.
	 * (In most cases this is the number of players who start the game)
	 */
	private final int maxPlayerCount;
	/**
	 * All currently connected players.
	 */
	private LinkedList<Player> playerList;
	/**
	 * The current deck of hidden cards left.
	 */
	private LinkedList<Card> cards;
	/**
	 * The type of card deck that will be used for the game.
	 */
	private final CardValue[] cardDeckType;
	/**
	 * The player whose turn it is.
	 */
	private Player activePlayer;
	/**
	 * The player who is dealer for this round.
	 */
	private Player dealer;

	/**
	 * Construct a new game.
	 * @param pPlayerCount Number of players for this game
	 * @param pCardDeck The type of card deck that is used
	 */
	public Gameplay(final int pPlayerCount, final CardValue[] pCardDeck) {
		cardDeckType = pCardDeck;
		maxPlayerCount = pPlayerCount;
		playerList = new LinkedList<Player>();
	}

	/**
	 * Create a new, ordered card deck that contains all values
	 * set in {@link #cardDeckType} in all four colors.
	 */
	protected final void createCardDeck() {
		cards = new LinkedList<Card>();
		for (CardColor color : CardColor.values()) {
			for (CardValue value : cardDeckType) {
				cards.add(new Card(color, value));
			}
		}
	}

	/**
	 * Take a random card from the ordered card deck.
	 * @return The taken card, or null if there are no cards left on the deck
	 */
	public final Card takeCard() {
		if (cards.size() == 0) {
			return null;
		}
		int num = (int) (Math.random() * cards.size());
		Card c = cards.get(num);
		cards.remove(num);
		return c;
	}

	/**
	 * Add a new player to the game.
	 * @param p The player to be added
	 * @return true on success, false when the maximum number
	 * of players was already reached before
	 */
	public final boolean addPlayer(final Player p) {
		if (playerList.size() == maxPlayerCount) {
			return false;
		}
		playerList.add(p);
		return true;
	}

	/**
	 * Get the player who is to make his turn after the given player.
	 * @param p The player whose next to search
	 * @return The next player
	 */
	protected final Player nextPlayerFor(final Player p) {
		int nextPos = playerList.indexOf(p) + 1;
		if (nextPos == playerList.size()) {
			nextPos = 0;
		}
		Player next = playerList.get(nextPos);		
		if (next.getLifes() > 0) {
			return next;
		} else {
			return nextPlayerFor(next);
		}
	}	
	
	/**
	 * @return All players currently in the game
	 */
	public final LinkedList<Player> getPlayers() {
		return playerList;
	}

	/**
	 * @return The player whose turn it is
	 */
	public final Player getCurrentPlayer() {
		return activePlayer;
	}
	
	/**
	 * Set a new active player.
	 * @param pPlayer New player
	 */
	public final void setCurrentPlayer(final Player pPlayer) {
		if (!playerList.contains(pPlayer)) {
			throw new IllegalArgumentException(
					"Active player was not found in player list");
		}
		this.activePlayer = pPlayer;
	}

	/**
	 * @return The player who is currently dealer
	 */
	public final Player getDealer() {
		return dealer;
	}
	
	/**
	 * Set a player to be the new dealer.
	 * @param pDealer New dealer
	 */
	public final void setDealer(final Player pDealer) {
		if (!playerList.contains(pDealer)) {
			throw new IllegalArgumentException(
					"Dealer was not found in player list");
		}
		dealer = pDealer;
	}

	/**
	 * @return The maximum number of players in this game
	 */
	public final int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	/**
	 * @return The player at the local host device
	 */
	public final LocalPlayer getHostPlayer() {
		for (Player p : getPlayers()) {
			if (p.getClass().equals(LocalPlayer.class)) {
				return (LocalPlayer) p;
			}
		}		
		return null;
	}

	/**
	 * Handle an incoming message.
	 * @param msg Incoming message
	 */
	public abstract void checkMessage(String msg);
}