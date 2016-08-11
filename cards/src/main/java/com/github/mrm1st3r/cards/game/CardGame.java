package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * This abstract class describes the general functions of a card game.
 * 
 * @author Sergius Maier, Lukas 'mrm1st3r' Taake
 * @version 1.1.0
 */
public abstract class CardGame {

	/**
	 * The maximum number of players in this game. (In most cases this is the
	 * number of players who start the game)
	 */
	private final int maxPlayerCount;
	/**
	 * All currently connected players.
	 */
	private final LinkedList<Player> mPlayerList;
	/**
	 * The type of card deck that will be used for the game.
	 */
	private final CardValue[] mCardDeckType;

	/**
	 * The current deck of hidden cards left.
	 */
	private LinkedList<Card> mHiddenCards;
	/**
	 * The cards laying openly on the table.
	 */
	private CardDeck mTableCards;
	/**
	 * The player whose turn it is.
	 */
	private Player mActivePlayer;
	/**
	 * The player who is dealer for this round.
	 */
	private Player mDealer;
	/**
	 * Flag to indicate if the game is still in progress.
	 */
	private boolean mPlayingFlag = true;

	/**
	 * Construct a new game.
	 * 
	 * @param pPlayerCount
	 *            Number of players for this game
	 * @param pCardDeck
	 *            The type of card deck that is used
	 */
	protected CardGame(final int pPlayerCount, final CardValue[] pCardDeck) {
		mCardDeckType = pCardDeck;
		maxPlayerCount = pPlayerCount;
		mPlayerList = new LinkedList<Player>();
		mTableCards = new CardDeck();
	}

	/**
	 * Set a random player to be the dealer and start the game.
	 */
	public final void start() {
		mPlayingFlag = true;

		onStart();

		while (mPlayingFlag) {
			onBeforePlay();
			onPlay();
			onAfterPlay();
		}

		onEnd();
	}

	/**
	 * Action that is performed when the game is started.
	 */
	protected abstract void onStart();

	/**
	 * Action that is performed before every game round.
	 */
	protected void onBeforePlay() {

	}

	/**
	 * Action that is performed each game round.
	 */
	protected abstract void onPlay();

	/**
	 * Action that is performed after every game round.
	 */
	protected void onAfterPlay() {

	}

	/**
	 * Action that is performed when the game is finished.
	 */
	protected abstract void onEnd();

	/**
	 * Create a new, ordered card deck that contains all values set in
	 * {@link #mCardDeckType} in all four colors.
	 */
	protected final void createCardDeck() {
		mHiddenCards = new LinkedList<Card>();

		for (CardColor color : CardColor.values()) {
			for (CardValue value : mCardDeckType) {
				mHiddenCards.add(new Card(color, value));
			}
		}
	}

	/**
	 * Take a random card from the ordered card deck to avoid randomizing more
	 * than necessary.
	 * 
	 * @return The taken card, or null if there are no cards left on the deck
	 */
	public final Card takeCardFromDeck() {
		if (mHiddenCards.size() == 0) {
			return null;
		}
		int num = (int) (Math.random() * mHiddenCards.size());
		Card c = mHiddenCards.get(num);
		mHiddenCards.remove(num);
		return c;
	}

	/**
	 * Add a new player to the game.
	 * 
	 * @param p
	 *            The player to be added
	 * @return true on success, false when the maximum number of players was
	 *         already reached before
	 */
	public final boolean addPlayer(final Player p) {
		if (mPlayerList.size() == maxPlayerCount) {
			return false;
		}
		mPlayerList.add(p);
		return true;
	}

	/**
	 * Get the player who is to make his turn after the given player.
	 * 
	 * @param p
	 *            The player whose next to search
	 * @return The next player
	 */
	protected final Player nextPlayerFor(final Player p) {
		if (mPlayerList.size() < 2) {
			return null;
		}

		int nextPos = mPlayerList.indexOf(p) + 1;
		if (nextPos == mPlayerList.size()) {
			nextPos = 0;
		}
		Player next = mPlayerList.get(nextPos);
		if (next.getLifes() >= 0) {
			return next;
		} else {
			return nextPlayerFor(next);
		}
	}

	/**
	 * Get all currently connected players.
	 * 
	 * @return All players currently in the game
	 */
	public final LinkedList<Player> getPlayers() {
		return mPlayerList;
	}

	/**
	 * Get the player whose turn it is at the moment.
	 * 
	 * @return The player whose turn it is
	 */
	public final Player getCurrentPlayer() {
		return mActivePlayer;
	}

	/**
	 * Set a new active player.
	 * 
	 * @param pPlayer
	 *            New player
	 */
	public final void setCurrentPlayer(final Player pPlayer) {
		if (!mPlayerList.contains(pPlayer)) {
			throw new IllegalArgumentException(
					"Active player was not found in player list");
		}
		this.mActivePlayer = pPlayer;
	}

	/**
	 * Get the current dealer.
	 * 
	 * @return The player who is currently dealer
	 */
	public final Player getDealer() {
		return mDealer;
	}

	/**
	 * Set a player to be the new dealer.
	 * 
	 * @param pDealer
	 *            New dealer
	 */
	public final void setDealer(final Player pDealer) {
		if (!mPlayerList.contains(pDealer)) {
			throw new IllegalArgumentException(
					"Dealer was not found in player list");
		}
		mDealer = pDealer;
	}

	/**
	 * Get the maximum number of players.
	 * 
	 * @return The maximum number of players in this game
	 */
	public final int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	/**
	 * Get the player who is playing at the host device.
	 * 
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
	 * Get all cards laying openly on the table.
	 * 
	 * @return Card deck containing all cards on the table
	 */
	public final CardDeck getTableCards() {
		return mTableCards;
	}

	/**
	 * Get the current progress state.
	 * 
	 * @return true if game is in progress, false otherwise
	 */
	public final boolean inProgress() {
		return mPlayingFlag;
	}

	/**
	 * Set the game to end at the next possible state.
	 */
	public final void endGame() {
		mPlayingFlag = false;
	}

	/**
	 * Send a broadcast message to all connected players.
	 * 
	 * @param msg
	 *            broadcast message to send
	 */
	protected final void broadcast(final String msg) {
		for (Player p : getPlayers()) {
			p.command(msg);
		}
	}

	/**
	 * Handle a message that is coming from a player.
	 * 
	 * @param p
	 *            The player who sent the message
	 * @param msg
	 *            Incoming message
	 */
	public abstract void handleMessage(Player p, String msg);
}
