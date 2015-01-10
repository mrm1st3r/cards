package com.github.mrm1st3r.cards.game;

<<<<<<< HEAD
import java.util.Random;

import com.github.mrm1st3r.cards.R;
=======
import android.util.Log;
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58

/**
 * This class describes the game rules and sequence for the game "Schwimmen".
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public class ThirtyOne extends Gameplay {

<<<<<<< HEAD
=======
	/**
	 * Debug tag.
	 */
	private static final String TAG = ThirtyOne.class.getSimpleName();
	/**
	 * Number of lifes for each player to start with.
	 */
	public static final int MAX_LIFES = 3;
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
	public ThirtyOne(int m) {
		super(m, 32);
		setStopped(m + 1);
=======
	public ThirtyOne(final int pPlayerCount) {
		super(pPlayerCount, CardValue.getSkatDeck());
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
		String str;
		lastrd = true;
		/*
		 * creates the list of opponents individual for every player and send it
		 * to them
		 */
		for (int i = 0; i < playerCount; i++) {
			p = players[i];
			int j = i;
			j = nextPlayer(i);
			str = play;
			while (j != i) {
				str = str + " " + p.getName();
				j = nextPlayer(j);
=======

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
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
			}
			p.connect(str);
			p.connect("inactive");
		}
<<<<<<< HEAD
		updateMessage("Der Dealer ist dabei sich zu entscheiden");
		for (int i = 0; i < playerCount; i++) {
			if (i != dealer && players[i].getLife() >= 0) {
				p = players[i];
				giveHand(p);
				p.connect("hand " + p.hand[0] + " " + p.hand[1] + " "
						+ p.hand[2]);
				updateScore(p);
				p.connect("score " + p.getScore());
				p.connect("life " + p.getLife());
=======

		updateMessage("Der Dealer ist dabei sich zu entscheiden");
		for (Player p : getPlayers()) {

			if (p.getLifes() >= 0) {

				giveHand(p);

				Card[] hand = p.getHand();

				p.sendMessage("hand " + hand[0].getImageName()
						+ " " + hand[1].getImageName() + " "
						+ hand[2].getImageName());
				p.sendMessage("life " + p.getLifes());
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
			}
		}
		Player dealer = getDealer();
		setCurrentPlayer(dealer);
		choice();
<<<<<<< HEAD
		p.connect("updateHand " + p.hand[0] + " " + p.hand[1] + " " + p.hand[2]);
		p.connect("takechoice");
		synchronized (playerLock) {
=======

		dealer.sendMessage("takechoice");
		synchronized (dealer.getLock()) {
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
		while (stopped > playerCount) {
			updateMessage(players[currP].getName() + " ist an der Reihe");
			players[currP].connect("active");
			synchronized (playerLock) {
=======
		Player p = getCurrentPlayer();

		while (!p.equals(stopper) && winner == null) {
			updateMessage(p.getName() + " ist an der Reihe");
			p.sendMessage("active");
			synchronized (p.getLock()) {
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
				try {
					p.getLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
<<<<<<< HEAD
			currP = nextPlayer(currP);
		}
		if (lastrd) {
			do {
				players[currP].connect("lastround");
				synchronized (playerLock) {
					try {
						playerLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				currP = nextPlayer(currP);
			} while (currP != stopped);
=======
			p = nextPlayerFor(p);
			setCurrentPlayer(p);
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
		float result = 0;
		Player p;
		Player[] oldPlayers = players;
		int alive = playerCount;
		for (int i = 0; i < playerCount; i++) {
			p = players[i];
			if (p.getLife() >= 0) {
				result = Math.min(result, p.getScore());
			}
		}
		for (int i = 0; i < playerCount; i++) {
			p = players[i];
			if (p.getLife() >= 0 && p.getScore() == result) {
				if (p.getLife() > 0) {
					p.decreaseLife();
				}
			}
			if (p.getLife() < 0) {
				alive--;
			}
=======
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
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
		}

		int alive = countLivingPlayers();
		Player host = getHostPlayer();

		if (alive == 1) {
<<<<<<< HEAD
			p = null;
			int i = 0;
			while (p == null) {
				if (players[i].getLife() >= 0) {
					p = players[i];
=======
			for (Player p : getPlayers()) {
				if (p.getLifes() >= 0) {
					updateMessage(p.getName() + " hat gewonnen");
					break;
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
				}
			}

			playing = false;
<<<<<<< HEAD
			players[0].connect("newgame");
			synchronized (playerLock) {
				try {
					playerLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else if (alive == 0) {
			String str = "Unentschieden zwischen";
			p = null;
			for (int i = 0; i < playerCount; i++) {
				p = oldPlayers[i];
				if (p.getLife() >= 0) {
					str = str + " " + p.getName();
=======
			host.sendMessage("newgame");

		} else if (alive == 0) {
			String str = "Unentschieden zwischen";

			for (Player p : getPlayers()) {
				if (p.getLifes() >= 0) {
					str += " " + p.getName();
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
				}
			}
			updateMessage(str);
			playing = false;
<<<<<<< HEAD
			players[0].connect("newgame");
			synchronized (playerLock) {
				try {
					playerLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			players[0].connect("nextround");
			synchronized (playerLock) {
				try {
					playerLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
=======
			host.sendMessage("newgame");
		} else {
			host.sendMessage("nextroundchoice");
		}

		synchronized (host.getLock()) {
			try {
				host.getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
	 * 
	 */
<<<<<<< HEAD
	private int nextPlayer(int p) {
		int temp = 0;
		if (p >= playerCount) {
			temp = 0;
		} else {
			temp = p++;
		}
		if (players[temp].getLife() < 0) {
			temp = nextPlayer(temp);
=======
	private void choice() {
		choice = new Card[HAND_SIZE];
		for (int i = 0; i < HAND_SIZE; i++) {
			choice[i] = takeCard();
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
		}
	}

	/**
	 * Es werden {@link hmax} Karten vom Stapel genommen und in die Hand des
	 * Spielers hinzugef�gt.
	 * 
	 * @param p
	 *            Spieler
	 */
<<<<<<< HEAD
	public void giveHand(Player p) {
		boolean temp = true;
		for (int i = 0; i < hmax; i++) {
			temp = p.add2Hand(takeCard());
			if (temp) {
				i = hmax;
			}
=======
	public final void giveHand(final Player p) {

		for (int i = 0; i < HAND_SIZE; i++) {
			p.addToHand(takeCard());
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
		}
	}

	/**
	 * Aktualisiert die Punkte aller Spieler.
	 */
<<<<<<< HEAD
	public void updateScores() {
		for (Player p : players) {
			if (p.getLife() >= 0) {
=======
	public final void updateScores() {
		for (Player p : getPlayers()) {
			if (p.getLifes() >= 0) {
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
		float clover = 0;
		float same = 0;
		float result = 0;
		for (int i = 0; i < c.length; i++) {
			switch (c[i].getColour()) {
			case 0:
				hearts += c[i].getValue();
				break;
			case 1:
				diamonds += c[i].getValue();
				break;
			case 2:
				spades += c[i].getValue();
				break;
			case 3:
				clover += c[i].getValue();
				break;
			}
		}
		if (c[0].getName() == c[1].getName()
				&& c[0].getName() == c[2].getName()) {
			same = (float) 30.5;
			if (c[0].getName() == "ace") {
				result = 33;
			}
		}
		result = Math.max(result, hearts);
		result = Math.max(result, diamonds);
		result = Math.max(result, spades);
		result = Math.max(result, clover);
		result = Math.max(result, same);
		if (result >= 31) {
			lastrd = false;
			stopped = currP;
		}
		return result;
	}
=======
		float clubs = 0;
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58

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
<<<<<<< HEAD
		if (parts[0] == "swap") {
			swapCards(players[currP].hand, table, Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2]));
			updateTables();
			updateHand();
		} else if (parts[0] == "swapall") {
			for (int i = 0; i < hmax; i++) {
				swapCards(table, players[currP].hand, i, i);
			}
=======
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
			
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
			currP = nextPlayer(dealer);
		} else if (parts[0] == "nextround") {
			if (parts[1] == "yes") {
				for (int j = 0; j < playerCount; j++) {
					players[j].connect("nextround");
=======
			setCurrentPlayer(nextPlayerFor(getDealer()));
		} else if (parts[0].equals("nextround")) {
			if (parts[1].equals("yes")) {
				for (Player p : getPlayers()) {
					p.sendMessage("nextround");
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
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
<<<<<<< HEAD
		for (int i = 0; i < hmax; i++) {
			str = str + " " + table[i].getImage();
		}
		for (int i = 0; i < playerCount; i++) {
			Player p = players[i];
			p.connect(str);
=======
		for (Card c : table) {
			str = str + " " + c.getImageName();
		}
		Log.d(TAG, str);
		for (Player p : getPlayers()) {
			p.sendMessage(str);
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
		}
	}

	/**
	 * Send a message to the current player with his new hand cards.
	 * @param p The player who should receive new hand cards
	 */
	private void updateHand(final Player p) {
		updateScore(p);
		String str = "hand";
<<<<<<< HEAD
		Player p = players[currP];
		for (int i = 0; i < hmax; i++) {
			str = str + " " + p.hand[i].getImage();
		}
		p.connect(str);
		updateScore(p);
		p.connect("score " + p.getScore());
	}

	private void updateMessage(String msg) {
		for (int i = 0; i < playerCount; i++) {
			Player p = players[i];
			p.connect("msg " + msg);
=======
		
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
>>>>>>> 095ad206f97354132a8fe1775b1f858e93777d58
		}
	}
}
