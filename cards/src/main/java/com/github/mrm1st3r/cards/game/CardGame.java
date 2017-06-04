package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * This abstract class describes the general functions of a card game.
 */
public abstract class CardGame {

	private final int maxPlayerCount;
	private final LinkedList<Player> mPlayerList;
	private final CardValue[] mCardDeckType;

	private LinkedList<Card> hiddenTableCards;
	private CardDeck openTableCards;
	private Player activePlayer;
	private Player currentDealer;
	private boolean mPlayingFlag = true;

	CardGame(int pPlayerCount, CardValue[] pCardDeck) {
		mCardDeckType = pCardDeck;
		maxPlayerCount = pPlayerCount;
		mPlayerList = new LinkedList<>();
		openTableCards = new CardDeck();
	}

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
	protected void onBeforePlay() {}

	/**
	 * Action that is performed each game round.
	 */
	protected abstract void onPlay();

	/**
	 * Action that is performed after every game round.
	 */
	protected void onAfterPlay() {}

	/**
	 * Action that is performed when the game is finished.
	 */
	protected abstract void onEnd();

	void createCardDeck() {
		hiddenTableCards = new LinkedList<>();
		for (CardColor color : CardColor.values()) {
			for (CardValue value : mCardDeckType) {
				hiddenTableCards.add(new Card(color, value));
			}
		}
	}

	Card takeCardFromDeck() {
		if (hiddenTableCards.size() == 0) {
			return null;
		}
		int num = (int) (Math.random() * hiddenTableCards.size());
		Card c = hiddenTableCards.get(num);
		hiddenTableCards.remove(num);
		return c;
	}

	public boolean addPlayer(Player p) {
		if (mPlayerList.size() == maxPlayerCount) {
			return false;
		}
		mPlayerList.add(p);
		return true;
	}

	Player nextPlayerFor(Player p) {
		if (mPlayerList.size() < 2) {
			return null;
		}
		int nextPos = mPlayerList.indexOf(p) + 1;
		if (nextPos == mPlayerList.size()) {
			nextPos = 0;
		}
		Player next = mPlayerList.get(nextPos);
		if (next.getLifesLeft() >= 0) {
			return next;
		} else {
			return nextPlayerFor(next);
		}
	}

	public LinkedList<Player> getPlayers() {
		return mPlayerList;
	}

	Player getCurrentPlayer() {
		return activePlayer;
	}

	void setCurrentPlayer(Player pPlayer) {
		if (!mPlayerList.contains(pPlayer)) {
			throw new IllegalArgumentException("Active player was not found in player list");
		}
		this.activePlayer = pPlayer;
	}
	Player getDealer() {
		return currentDealer;
	}

	void setDealer(Player pDealer) {
		if (!mPlayerList.contains(pDealer)) {
			throw new IllegalArgumentException("Dealer was not found in player list");
		}
		currentDealer = pDealer;
	}

	LocalPlayer getHostPlayer() {
		for (Player p : getPlayers()) {
			if (p.getClass().equals(LocalPlayer.class)) {
				return (LocalPlayer) p;
			}
		}
		return null;
	}

	CardDeck getTableCards() {
		return openTableCards;
	}

	void endGame() {
		mPlayingFlag = false;
	}

	void broadcast(String msg) {
		for (Player p : getPlayers()) {
			p.command(msg);
		}
	}

	/**
	 * Handle a message that is coming from a player.
	 */
	public abstract void handleMessage(Player p, String msg);
}
