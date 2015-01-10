package com.github.mrm1st3r.cards.game;

import android.util.Log;

/**
 * This class describes the game rules and sequence for the game "Schwimmen".
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public class ThirtyOne extends Gameplay {

/**
	 * Debug tag.
	 */
	private static final String TAG = ThirtyOne.class.getSimpleName();
	/**
	 * Number of lifes for each player to start with.
	 */
	public static final int MAX_LIFES = 3;
	/**
	 * Number of cards that each player holds in hand and lay on the table.
	 */
	public static final int HAND_SIZE = 3;
	/**
	 * Maximum number of points that can be reached.
	 */
	public static final int POINTS_MAX = 33;
	/**
	 * Number of points that is achieved with three cards of the same value.
	 */
	public static final double POINTS_TRIPLE = 30.5;
	/**
	 * The number of points needed to instantly win a round.
	 */
	public static final int POINTS_WIN = 31;
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
	 * The hidden cards that the dealer will get if he refuses
	 * his hand cards. If the dealer accepts his hand cards, these
	 * will become the table cards.
	 */
	private Card[] choice = new Card[HAND_SIZE];
	/**
	 * Flag to indicate if the game is still in progress.
	 */
	private boolean playing = true;

	/**
	 * Construct a new game of "Schwimmen".
	 * @param pPlayerCount Number of players to begin with
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
	 * All players receive their hand cards and the dealer has to choose
	 * between two card sets of which he sees only one.
	 */
	public final void startRound() {
		String play = "players";
		for (Player p : getPlayers()) {
			p.setLifes(MAX_LIFES);
			p.setHand(new Card[HAND_SIZE]);
			
		}
		stopper = null;
		winner = null;
		createCardDeck();
		
		// send the list of opponents to each player
		for (Player p : getPlayers()) {
			Player next = nextPlayerFor(p);
			String str = play;

			while (!next.equals(p)) {
				str += " " + next.getName();
				next = nextPlayerFor(next);
			}
			p.connect(str);
			p.connect("inactive");
		}
		updateMessage("Der Dealer ist dabei sich zu entscheiden");
		for (Player p : getPlayers()) {

			if (p.getLifes() >= 0) {

				giveHand(p);

				Card[] hand = p.getHand();

				p.sendMessage("hand " + hand[0].getImageName()
						+ " " + hand[1].getImageName() + " "
						+ hand[2].getImageName());
				p.sendMessage("life " + p.getLifes());
			}
		}
		Player dealer = getDealer();
		setCurrentPlayer(dealer);
		choice();
		dealer.sendMessage("takechoice");
		synchronized (dealer.getLock()) {
			try {
				dealer.getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		updateScores();
	}

	/**
	 * Game sequence.<br>
	 * Alle Spieler sind nach einander dran bis einer die Runde beendet. Danach
	 * kommen die anderen Spieler noch mal an die Reihe.
	 */
	private void playRound() {
		Player p = getCurrentPlayer();

		while (!p.equals(stopper) && winner == null) {
			updateMessage(p.getName() + " ist an der Reihe");
			p.sendMessage("active");
			synchronized (p.getLock()) {
				try {
					p.getLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			p = nextPlayerFor(p);
			setCurrentPlayer(p);
		}
	}

	/**
	 * Ende einer Runde.<br>
	 * Ermittelt den oder die Spieler mit der niedrigsten Punktezahl, die dann
	 * ein Leben verlieren. Entweder gibt es einen Gewinner, Unendschieden oder
	 * es werden die Spieler aus dem Spiel genommen, die keine Leben mehr haben
	 * und es wird eine neue Runde gestartet.
	 */
	public final void endRound() {
		updateScores();
		float minScore = POINTS_MAX;

		// search for the lowest score in this round
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				minScore = Math.min(minScore, p.getScore());
			}
		}

		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0 && p.getScore() == minScore) {
				if (p.getLifes() > 0) {
					p.decreaseLife();
				}
			}
		}

		int alive = countLivingPlayers();
		Player host = getHostPlayer();

		if (alive == 1) {
			for (Player p : getPlayers()) {
				if (p.getLifes() >= 0) {
					updateMessage(p.getName() + " hat gewonnen");
					break;
				}
			}

			playing = false;
			host.sendMessage("newgame");

		} else if (alive == 0) {
			String str = "Unentschieden zwischen";

			for (Player p : getPlayers()) {
				if (p.getLifes() >= 0) {
					str += " " + p.getName();
				}
			}
			updateMessage(str);
			playing = false;
			host.sendMessage("newgame");
		} else {
			host.sendMessage("nextroundchoice");
		}

		synchronized (host.getLock()) {
			try {
				host.getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Dem Dealer wird die M�glichkeit gegeben sich zwischen zwei H�nden zu
	 * entscheiden. Die Hand f�r die er sich nicht entschieden hat wird auf
	 * den Tisch gelegt.
	 * @param str Choice made by the dealer
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
	 * Creates a second hand of cards for the dealer
	 */
	private void choice() {
		choice = new Card[HAND_SIZE];
		for (int i = 0; i < HAND_SIZE; i++) {
			choice[i] = takeCard();
		}
	}

	/**
	 * Es werden {@link hmax} Karten vom Stapel genommen und in die Hand des
	 * Spielers hinzugef�gt.
	 * 
	 * @param p
	 *            Spieler
	 */
	public final void giveHand(final Player p) {

		for (int i = 0; i < HAND_SIZE; i++) {
			p.addToHand(takeCard());
		}
	}

	/**
	 * Aktualisiert die Punkte aller Spieler.
	 */
	public final void updateScores() {
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
				updateScore(p);
				p.sendMessage("score " + p.getScore());
				if (p.getScore() >= POINTS_WIN) {
					winner = p;
				}
			}
		}
	}

	/**
	 * Aktualisiert die Punkte eines Spielers.
	 * 
	 * @param p
	 *            Spieler
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
		p.setScore(score);
	}

	@Override
	public final void checkMessage(final String msg) {
		
		Log.d(TAG, "game receiving: " + msg);

		String[] parts = msg.split(" ");
		Player current = getCurrentPlayer();
		
		if (parts[0] .equals("swap")) {
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
			} else {
				playing = false;
			}
		} else if (parts[0].equals("newgame")) {
			if (parts[1].equals("no")) {
				playing = false;
			} // no action needed if yes
		}
	}



	/**
	 * Send a broadcast to all players to update the cards on the table.
	 */
	private void updateTables() {
		String str = "table";
		for (Card c : table) {
			str = str + " " + c.getImageName();
		}
		Log.d(TAG, str);
		for (Player p : getPlayers()) {
			p.sendMessage(str);
		}
	}

	/**
	 * Send a message to the current player with his new hand cards.
	 * @param p The player who should receive new hand cards
	 */
	private void updateHand(final Player p) {
		updateScore(p);
		String str = "hand";
		for (Card c : p.getHand()) {
			str = str + " " + c.getImageName();
		}
		p.sendMessage(str);

		p.sendMessage("score " + p.getScore());
	}

	/**
	 * Send a broadcast with a text message to all players.
	 * @param msg Message to send
	 */
	private void updateMessage(final String msg) {
		for (Player p : getPlayers()) {
			p.sendMessage("msg " + msg);
		}
	}
}
