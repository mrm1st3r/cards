package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

import android.util.Log;

/**
 * This class describes the game rules and sequence for the game "Thirty-One".
 * 
 * @author Sergius Maier
 * @version 1.0
 */
public class ThirtyOne extends Gameplay {

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
	private static final double POINTS_TRIPLE = 30.5;
	/**
	 * The number of points needed to instantly win a round.
	 */
	private static final int POINTS_WIN = 31;
	/**
	 * All cards laying openly on the table.
	 */
	private Card[] table = new Card[HAND_SIZE];
	/**
	 * The player who has closed the round.
	 */
	private Player stopper = null;
	/**
	 * The player who has won the round.
	 */
	private Player winner = null;
	/**
	 * The hidden cards that the dealer will get if he refuses his hand cards.
	 * If the dealer accepts his hand cards, these will become the table cards.
	 */
	private Card[] choice = new Card[HAND_SIZE];
	/**
	 * Flag to indicate if the game is still in progress.
	 */
	private boolean playing = true;

	/**
	 * Construct a new game of "Thirty-One".
	 * 
	 * @param pPlayerCount
	 *            Number of players to begin with
	 */
	public ThirtyOne(final int pPlayerCount) {
		super(pPlayerCount, CardValue.getSkatDeck());
	}

	/**
	 * Set a random player to be the dealer and start the game.
	 */
	public final void start() {
		// the host is always dealer in the first round
		setDealer(getHostPlayer());
		while (playing) {
			startRound();
			playRound();
			endRound();
		}
	}

	/**
	 * The start of a game round.<br>
	 * All players receive their hand cards and the dealer has to choose between
	 * two card sets of which he sees only one.
	 */
	public final void startRound() {
		// initialization
		String play = "players";
		for (Player p : getPlayers()) {
			p.setHand(new Card[HAND_SIZE]);
		}
		stopper = null;
		winner = null;
		createCardDeck();

		for (Player p : getPlayers()) {
			// all players receive their points
			p.sendMessage("life " + p.getLifes());
			
			if (p.getLifes() >= 0) {
				// all players who have still lives get hand cards
				giveHand(p);
				Card[] hand = p.getHand();
				p.sendMessage("hand " + hand[0].getImageName() + " "
						+ hand[1].getImageName() + " "
						+ hand[2].getImageName());
				
				// opponents for all players with points
				Player next = nextPlayerFor(p);
				String str = play;
				while (!next.equals(p)) {
					str += " " + next.getName();
					next = nextPlayerFor(next);
				}
				p.sendMessage(str);
				p.sendMessage("inactive");
			}
		}
		// the dealer is given the choice between two hand cards
		updateMessage("Der Dealer ist dabei sich zu entscheiden");
		Player dealer = getDealer();
		setCurrentPlayer(dealer);
		choice();
		dealer.sendMessage("takechoice");
		// waiting for dealers choice
		synchronized (dealer.getLock()) {
			try {
				dealer.getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// update the scores of all player
		updateScores();
	}

	/**
	 * Game sequence.<br>
	 * All players are successively tuned till an active player finishes the
	 * round. All other players get the possibility to do one last action.
	 */
	private void playRound() {
		Player p = getCurrentPlayer();
		while (!p.equals(stopper) && winner == null) {
			updateMessage(p.getName() + " ist an der Reihe");
			if (stopper == null) {
				p.sendMessage("active");
			} else {
				p.sendMessage("lastround");
			}
			synchronized (p.getLock()) {
				try {
					p.getLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			p = nextPlayerFor(p);
			if (p == null) {
				playing = false;
				throw new IllegalStateException(
						"Game with less than two players");
			}
			setCurrentPlayer(p);
		}
	}

	/**
	 * End of a round.<br>
	 * Gets one or more players with the lowest score. These players lose a
	 * life. Either there is a winner, a draw or the players are removed from
	 * the game, who have no life and it starts a new round/game.
	 */
	public final void endRound() {
		updateScores();
		float minScore = POINTS_MAX;
		float maxScore = 0;
		LinkedList<Player> oldPlayers = getPlayers();
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
				p.decreaseLife();
			}
			// all players might have same points
			if (winner == null && p.getScore() == maxScore) {
				// someone closed with less than 31 points
				winner = p;
			}
			Log.d(TAG, p.toString());
		}
		int alive = countLivingPlayers();
		Player host = getHostPlayer();
		Log.d(TAG, alive + " players alive");
		// winner
		if (alive == 1) {
			for (Player p : getPlayers()) {
				if (p.getLifes() >= 0) {
					updateMessage(p.getName() + " hat das Spiel gewonnen");
				}
			}

			host.sendMessage("newgame");
			// draw
		} else if (alive == 0) {
			String str = "Unentschieden zwischen";
			for (Player p : oldPlayers) {
				if (p.getLifes() >= 0) {
					str += " " + p.getName();
				}
			}
			updateMessage(str);

			host.sendMessage("newgame");
		} else {
			updateMessage(winner.getName() + " hat die Runde gewonnen");
			host.sendMessage("nextroundchoice");
		}
		// wait for host choice for new round/game
		synchronized (host.getLock()) {
			try {
				Log.d(TAG, "waiting for " + host.getLock());
				host.getLock().wait();
				Log.d(TAG, "received notification from " + host.getLock());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Player p takes 3 Cards from deck and add it to his hand.
	 * 
	 * @param p
	 *            Player who gets cards from deck to his hand
	 */
	public final void giveHand(final Player p) {
		for (int i = 0; i < HAND_SIZE; i++) {
			p.addToHand(takeCard());
		}
	}

	/**
	 * Creates a second hand of cards for the dealer.
	 */
	private void choice() {
		choice = new Card[HAND_SIZE];
		for (int i = 0; i < HAND_SIZE; i++) {
			choice[i] = takeCard();
		}
	}

	/**
	 * Count the number of players with lifes.
	 * 
	 * @return The number of living players left
	 */
	public final int countLivingPlayers() {
		int num = 0;
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				num++;
			}
		}
		return num;
	}

	/**
	 * Send a broadcast with a text message to all players.
	 * 
	 * @param msg
	 *            Message to send
	 */
	private void updateMessage(final String msg) {
		for (Player p : getPlayers()) {
			p.sendMessage("msg " + msg);
		}
	}

	/**
	 * Update the score of all players.
	 */
	public final void updateScores() {
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				updateScore(p);
			}
		}
	}

	/**
	 * Update the score of one player.
	 * 
	 * @param p
	 *            Player whose score to update
	 */
	public final void updateScore(final Player p) {
		Card[] hand = p.getHand();
		if (hand[0] == null) {
			return;
		}
		float hearts = 0;
		float diamonds = 0;
		float spades = 0;
		float clubs = 0;
		float score = 0;
		// check for three same values
		if (hand[0].getValue() == hand[1].getValue()
				&& hand[0].getValue() == hand[2].getValue()) {
			if (hand[0].getValue() == CardValue.ACE) {
				score = POINTS_MAX;
			} else {
				score = (float) POINTS_TRIPLE;
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

		if (score >= POINTS_WIN) {
			winner = p;
		}
		p.setScore(score);
		p.sendMessage("score " + p.getScore());
	}

	@Override
	public final void checkMessage(final Player player, final String msg) {
		Log.d(TAG, "game receiving: " + msg);
		String[] parts = msg.split(" ");
		Player current = getCurrentPlayer();
		if (parts[0].equals("left")) {
			getPlayers().remove(player);
		}
		if (parts[0].equals("swap")) {
			int handPos = Integer.parseInt(parts[1]);
			int tablePos = Integer.parseInt(parts[2]);
			Card temp = current.getHandCard(handPos);
			current.getHand()[handPos] = table[tablePos];
			table[tablePos] = temp;
			updateTables();
			updateHand(current);
		} else if (parts[0].equals("swapall")) {
			Card[] temp = current.getHand();
			current.setHand(table);
			table = temp;
			updateTables();
			updateHand(current);
		} else if (parts[0].equals("close")) {
			if (stopper == null) {
				stopper = current;
			}
		} else if (parts[0].equals("push")) {
			Log.i(TAG, current.getName() + " has pushed");
		} else if (parts[0].equals("choice")) {
			choiceResult(parts[1]);
			setCurrentPlayer(nextPlayerFor(getDealer()));
		} else if (parts[0].equals("nextround")) {
			if (parts[1].equals("yes")) {
				for (Player p : getPlayers()) {
					p.sendMessage("nextround");
				}
				setDealer(nextPlayerFor(getDealer()));
			} else if (parts[1].equals("no")) {
				playing = false;
			}
		} else if (parts[0].equals("newgame")) {
			if (parts[1].equals("no")) {
				playing = false;
			} else if (parts[1].equals("yes")) {
				newGame();
			}
		}
		synchronized (player.getLock()) {
			player.getLock().notify();
			Log.d(TAG, "notified " + player.getLock().toString());
		}
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
		if (str.equals("hand")) {
			table = choice;
		} else {
			table = getDealer().getHand();
			getDealer().setHand(choice);
			updateHand(getDealer());
		}
		updateTables();
	}

	/**
	 * Send a broadcast to all players to update the cards on the table.
	 */
	private void updateTables() {
		String str = "table";
		for (Card c : table) {
			str = str + " " + c.getImageName();
		}

		for (Player p : getPlayers()) {
			p.sendMessage(str);
		}
	}

	/**
	 * Send a message to the current player with his new hand cards.
	 * 
	 * @param p
	 *            The player who should receive new hand cards
	 */
	private void updateHand(final Player p) {
		updateScore(p);
		String str = "hand";
		for (Card c : p.getHand()) {
			str = str + " " + c.getImageName();
		}
		p.sendMessage(str);
	}

	/**
	 * Preparation for a new game. (new dealer, all players get max lifes)
	 */
	private void newGame() {
		for (Player p : getPlayers()) {
			p.sendMessage("nextround");
			p.setLifes(MAX_LIFES);
		}
		stopper = null;
		winner = null;
		choice = new Card[HAND_SIZE];
		setDealer(nextPlayerFor(getDealer()));
	}
}