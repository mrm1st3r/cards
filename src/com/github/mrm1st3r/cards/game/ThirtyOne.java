package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

import android.util.Log;

/**
 * This class describes the game rules and sequence for the game "Thirty-One".
 * 
 * @author Sergius Maier, Lukas 'mrm1st3r' Taake
 * @version 1.1.0
 */
public class ThirtyOne extends CardGame {

	/**
	 * Number of lifes for each player to start with.
	 */
	public static final int MAX_LIFES = 3;
	/**
	 * Number of cards that each player holds in hand and lay on the table.
	 */
	public static final int HAND_SIZE = 3;
	/**
	 * Debug tag.
	 */
	private static final String TAG = ThirtyOne.class.getSimpleName();
	/**
	 * Maximum number of points that can be reached.
	 */
	private static final int POINTS_MAX = 33;
	/**
	 * Number of points that is achieved with three cards of the same value.
	 */
	private static final float POINTS_TRIPLE = 30.5f;
	/**
	 * The number of points needed to instantly win a round.
	 */
	private static final int POINTS_WIN = 31;

	/**
	 * Singleton.
	 */
	private static ThirtyOne instance = null;

	/**
	 * The player who has closed the round.
	 */
	private Player mStopper;
	/**
	 * The player who has won the round.
	 */
	private Player mWinner;
	/**
	 * The hidden cards that the dealer will get if he refuses his hand cards.
	 * If the dealer accepts his hand cards, these will become the table cards.
	 */
	private CardDeck mChoice;

	/**
	 * Create a new singleton.
	 * 
	 * @param pPlayerCount
	 *            Number of players for game
	 * @return New singleton
	 */
	public static ThirtyOne createInstance(final int pPlayerCount) {
		instance = new ThirtyOne(pPlayerCount);
		return instance;
	}

	/**
	 * Get the singleton.
	 * 
	 * @return The singleton
	 */
	public static ThirtyOne getInstance() {
		return instance;
	}

	/**
	 * Construct a new game of "Thirty-One".
	 * 
	 * @param pPlayerCount
	 *            Number of players to begin with
	 */
	protected ThirtyOne(final int pPlayerCount) {
		super(pPlayerCount, CardValue.getSkatDeck());
	}

	@Override
	protected final void onStart() {

		mStopper = null;
		mWinner = null;
		mChoice = new CardDeck();

		setDealer(getHostPlayer());
	}

	@Override
	protected final void onBeforePlay() {
		createCardDeck();

		for (Player p : getPlayers()) {

			if (p.getLifes() < 0) {
				continue;
			}

			// give new hand cards to all players
			fillHand(p.getHand());
			p.command(Command.encode("hand", p.getHand().getImages()));

			updateStatus(p);
		}

		// the dealer is given the choice between two hand cards
		broadcast(Command.encode("msg",
				"Der Dealer ist dabei sich zu entscheiden"));

		fillHand(mChoice);

		getDealer().command("takechoice");
		// waiting for dealers choice
		synchronized (getDealer().getLock()) {
			try {
				getDealer().getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected final void onPlay() {
		Player p = getCurrentPlayer();

		// a game of "thirty one" consists of multiple game rounds
		// one call of this method equals one whole round.

		while (!p.equals(mStopper) && mWinner == null) {
			broadcast(Command.encode("msg", p.getName() + " ist an der Reihe"));

			// activate player input and wait for response
			p.command("active");
			synchronized (p.getLock()) {
				try {
					p.getLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// calculate new player score
			p.setScore(calculateScore(p.getHand()));
			updateStatus(p);

			if (p.getScore() >= POINTS_WIN) {
				mWinner = p;
			}

			// move to the next player
			p = nextPlayerFor(p);
			setCurrentPlayer(p);

			if (p == null) {
				endGame();
				throw new IllegalStateException(
						"Game with less than two players");
			}
		}
	}

	@Override
	protected final void onAfterPlay() {

		float minScore = POINTS_MAX;
		float maxScore = 0;

		LinkedList<Player> losers = new LinkedList<Player>();

		// search for the lowest score in this round
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				minScore = Math.min(minScore, p.getScore());
				maxScore = Math.max(maxScore, p.getScore());
			}
		}

		// decrease the life of players with the lowest score
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0 && p.getScore() == minScore) {
				p.decreaseLifes();
			}

			// multiple players might have lowest points
			if (p.getScore() == minScore) {
				losers.add(p);
			}
		}

		if (countLivingPlayers() == 1) {
			endGame();
			return;
		}

		StringBuilder build = new StringBuilder();

		for (Player p : losers) {
			build.append(p.getName());
			build.append(", ");
		}

		broadcast(Command.encode("msg", build.toString() + " verliert/en"));
	}

	@Override
	protected final void onEnd() {
		if (mWinner != null) {
			broadcast(Command.encode("msg", mWinner.getName()
					+ " hat das Spiel gewonnen"));
		} else {
			// all players left the game
			Log.d(TAG, "All players left");
		}
	}

	@Override
	public final void handleMessage(final Player p, final String msg) {
		Log.d(TAG, "game receiving: " + msg);

		if (msg.length() == 0) {
			Log.d(TAG, "received empty message from" + p.getName());
			return;
		}

		Command comm = new Command(msg);

		// commands that might be sent by all players
		if (comm.equals("left") && p.getName().equals(comm.getArg(0))) {
			getPlayers().remove(p);
			broadcast(msg);
		}

		// commands that might only be sent by the active player
		if (p == getCurrentPlayer()) {

			if (comm.equals("swap")) {
				int handPos = Integer.parseInt(comm.getArg(0));
				int tablePos = Integer.parseInt(comm.getArg(1));
				Card handCard = p.getHand().getCard(handPos);
				Card tableCard = getTableCards().getCard(tablePos);

				p.getHand().replaceCard(handCard, tableCard);
				getTableCards().replaceCard(tableCard, handCard);

				updateTable();
				updateHand(p);

			} else if (comm.equals("swapall")) {
				p.getHand().swapWith(getTableCards());
				updateTable();
				updateHand(p);

			} else if (comm.equals("close")) {
				if (mStopper == null) {
					mStopper = p;
				}

			} else if (comm.equals("push")) {
				Log.i(TAG, p.getName() + " has pushed");
			}
		}

		if (p == getDealer() && comm.equals("choice")) {
			choiceResult(comm.getArg(0));
			setCurrentPlayer(nextPlayerFor(getDealer()));
		}

		// This method will be called not from the game thread, but from
		// a player thread (or the UI thread for local player)
		// after having performed an action, notify the game thread to continue
		// the game loop.
		synchronized (p.getLock()) {
			p.getLock().notify();
			Log.d(TAG, "notified " + p.getLock().toString());
		}
	}

	/**
	 * Place three cards from deck into this deck.
	 * 
	 * @param d
	 *            Deck to add cards to
	 */
	private void fillHand(final CardDeck d) {
		d.reset();
		for (int i = 0; i < HAND_SIZE; i++) {
			d.addCard(takeCardFromDeck());
		}
	}

	/**
	 * Count the number of players with lifes.
	 * 
	 * @return The number of living players left
	 */
	private int countLivingPlayers() {
		int num = 0;
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				num++;
			}
		}
		return num;
	}

	/**
	 * Calculate the score for a card deck.
	 * 
	 * @param d
	 *            Card deck to calculate score for
	 * @return calculated score
	 */
	private float calculateScore(final CardDeck d) {

		float hearts = 0;
		float diamonds = 0;
		float spades = 0;
		float clubs = 0;
		float score = 0;

		final Card[] hand = d.getAll();

		// check for three same values
		if (hand[0].getValue() == hand[1].getValue()
				&& hand[1].getValue() == hand[2].getValue()) {
			if (hand[0].getValue() == CardValue.ACE) {
				score = POINTS_MAX;
			} else {
				score = POINTS_TRIPLE;
			}

		} else {
			for (Card c : hand) {
				switch (c.getColor()) {
				case HEARTS:
					hearts += c.getIntValue();
					break;
				case DIAMONDS:
					diamonds += c.getIntValue();
					break;
				case SPADES:
					spades += c.getIntValue();
					break;
				case CLUBS:
					clubs += c.getIntValue();
					break;
				default:
					break;
				}
			}

			score = Math.max(score, hearts);
			score = Math.max(score, diamonds);
			score = Math.max(score, spades);
			score = Math.max(score, clubs);
		}

		return score;
	}

	/**
	 * Evaluation of the dealer's choice at the beginning of a round. If dealer
	 * choose hand the hidden cards would lay down on the table. Otherwise the
	 * dealer gets the hidden cards and the dealer's hand would lay down on the
	 * table.
	 * 
	 * @param str
	 *            Choice made by the dealer
	 */
	private void choiceResult(final String str) {
		if (str.equals("table")) {
			getDealer().getHand().swapWith(mChoice);
			updateHand(getDealer());
		}
		getTableCards().replaceDeck(mChoice);
		updateTable();
	}

	/**
	 * Send a broadcast to all players to update the cards on the table.
	 */
	private void updateTable() {
		broadcast(Command.encode("table", getTableCards().getImages()));
	}

	/**
	 * Send a message to the current player with his new hand cards.
	 * 
	 * @param p
	 *            The player who should receive hand cards
	 */
	private void updateHand(final Player p) {
		p.command(Command.encode("hand", p.getHand().getImages()));
	}

	/**
	 * Send a status message to a player.
	 * 
	 * @param p
	 *            Player to receive status
	 */
	private void updateStatus(final Player p) {
		final String status;
		if (p.getLifes() < 0) {
			status = "Ausgeschieden";
		} else {
			status = p.getScore() + " Punkte, " + p.getLifes() + " Leben";
		}
		p.command(Command.encode("status", status));
	}
}
