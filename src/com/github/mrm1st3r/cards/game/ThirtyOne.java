package com.github.mrm1st3r.cards.game;

import java.util.Random;

import com.github.mrm1st3r.cards.R;

/**
 * Erweitert die Klasse "Gameplay".<br>
 * Hauptklasse f�r das Spiel "ThirtyOne" / Schwimmen.
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public class ThirtyOne extends Gameplay {

	/**
	 * max. Anzahl der Karten, die ein Spieler in der Hand haben darf.
	 */
	int hmax = 3;
	/**
	 * Karten auf dem Tisch
	 */
	Card[] table = new Card[hmax];
	/**
	 * Zustand, der angibt, wer geklopft hat
	 */
	int stopped;
	Card[] choice;
	boolean playing = true;
	/**
	 * Konstruktor der Klasse "ThirtyOne".
	 * 
	 * @param m
	 *            Anzahl an Spielern
	 */
	public ThirtyOne(int m) {
		super(m, 32);
		setStopped(m + 1);
	}

	/**
	 * Ein Dealer wird per Zufall festgelegt und die erste Runde wird gestartet.
	 */
	public void start() {
		Random ran = new Random();
		dealer = ran.nextInt(max);
		game();
	}

	private void game(){
		while(playing){
			startRound();
			playRound();
			endRound();
		}
	}
	
	/**
	 * Start einer Runde.<br>
	 * Es werden Karten an alle Spieler ausgeteilt.<br>
	 * Der Dealer bekommt die M�glichkeit sich zwischen zwei H�nden zu
	 * entscheiden.<br>
	 * Der n�chste wird festgelegt und das Spiel gestartet.
	 */
	public void startRound() {
		Player p;
		String play = "players";
		String str;
		/*
		 * creates the list of opponents individual for every player
		 * and send it to them
		 */
		for (int i = 0; i < max; i++) {
			p = players[i];
			int j = i;
			j = nextPlayer(i);
			str = play;
			while (j != i) {
				str = str + " " + p.getName();
				j = nextPlayer(j);
			}
			p.connect(str);
		}
		for (int i = 0; i < max; i++) {
			if (i != dealer && players[i].getLife() >= 0) {
				p = players[i];
				giveHand(p);
				p.connect("hand " + p.hand[0] + " " + p.hand[1] + " "
						+ p.hand[2]);
				updateScore(p);
				p.connect("score " + p.getScore());
				p.connect("life " + p.getLife());
			}
		}
		p = players[dealer];
		currP = dealer;
		p.setHand(choice());
		currP = dealer;
		currP = nextPlayer(currP);
		game();
	}

	/**
	 * Spielablauf.<br>
	 * Alle Spieler sind nach einander dran bis einer die Runde beendet. Danach
	 * kommen die anderen Spieler noch mal an die Reihe.
	 */
	private void playRound() {
		int temp = 0;
		while (stopped > max) {
			// temp = turn();
			players[currP].connect("active");
			synchronized (playerLock) {
				try {
					playerLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (temp != max) {
				stopped = temp;
			}
			updateScore(players[currP]);
			currP = nextPlayer(currP);
		}
		do {
			// lastTurn();
			updateScore(players[currP]);
			currP = nextPlayer(currP);
		} while (currP != stopped);
		endRound();
	}

	/**
	 * Ende einer Runde.<br>
	 * Ermittelt den oder die Spieler mit der niedrigsten Punktezahl, die dann
	 * ein Leben verlieren. Entweder gibt es einen Gewinner, Unendschieden oder
	 * es werden die Spieler aus dem Spiel genommen, die keine Leben mehr haben
	 * und es wird eine neue Runde gestartet.
	 */
	public void endRound() {
		updateScores();
		float result = 0;
		Player p;
		int temp = max;
		for (int i = 0; i < max; i++) {
			p = players[i];
			result = Math.min(result, p.getScore());
		}
		for (int i = 0; i < max; i++) {
			p = players[i];
			if (p.getScore() == result) {
				if (p.getLife() > 0) {
					p.decreaseLife();
				} else {
					players[i] = null;
					temp--;
				}
			}
		}
		if (temp == 1) {
			// winner();
		} else if (temp == 0) {
			// draw();
		} else {
			Player[] tempP = new Player[temp];
			int j = 0;
			for (int i = 0; i < max; i++) {
				if (players[i] != null) {
					tempP[j] = players[i];
					j++;
				}
			}
			players = tempP;
			max = temp;
			dealer = nextPlayer(dealer);
			startRound();
		}
	}

	/**
	 * Dem Dealer wird die M�glichkeit gegeben sich zwischen zwei H�nden zu
	 * entscheiden. Die Hand f�r die er sich nicht entschieden hat wird auf
	 * den Tisch gelegt.
	 * 
	 * @return Hand, f�r die sich der Dealer entschieden hat
	 */
	private void choiceResult(int i) {
		if (i == 0) {
			table = choice;
		} else if (i == 1) {
			table = players[dealer].getHand();
			players[dealer].setHand(choice);
			updateHand();
		}
		updateTables();
	}

	private void choice() {
		choice = new Card[hmax];
		for (int i = 0; i < hmax; i++) {
			choice[i] = takeCard();	
		}
		giveHand(players[dealer]);
	}

	/**
	 * Determines the next current player.
	 * 
	 * @param p
	 *            current player
	 * @return next player
	 */
	private int nextPlayer(int p) {
		int temp = 0;
		if (p >= max) {
			temp = 0;
		} else {
			temp = p++;
		}
		if (players[temp].getLife() < 0) {
			temp = nextPlayer(temp);
		}
		return temp;
	}

	/**
	 * Es werden {@link hmax} Karten vom Stapel genommen und in die Hand des
	 * Spielers hinzugef�gt.
	 * 
	 * @param p
	 *            Spieler
	 */
	public void giveHand(Player p) {
		boolean temp = true;
		for (int i = 0; i < hmax; i++) {
			temp = p.add2Hand(takeCard());
			if (temp) {
				i = hmax;
			}
		}
	}

	/**
	 * Aktualisiert die Punkte aller Spieler.
	 */
	public void updateScores() {
		for (Player p : players) {
			updateScore(p);
		}
	}

	/**
	 * Aktualisiert die Punkte eines Spielers.
	 * 
	 * @param p
	 *            Spieler
	 */
	public void updateScore(Player p) {
		Card[] hand = p.getHand();
		p.setScore(calcScore(hand));
	}

	/**
	 * Kalkuliert den Punktestand aus der Hand, die �bergeben wurde.
	 * 
	 * @param c
	 *            Hand, von der die Punkte ermittelt werden soll
	 * @return kalkulierte Punktezahl
	 */
	private float calcScore(Card[] c) {
		float hearts = 0;
		float diamonds = 0;
		float spades = 0;
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
		return result;
	}

	/**
	 * Getter f�r {@link #hmax}
	 * 
	 * @return {@link #hmax}
	 */
	public int getHmax() {
		return hmax;
	}

	/**
	 * Setter f�r {@link #hmax}
	 * 
	 * @param hmax
	 *            um {@link #hmax} zu definieren
	 */
	public void setHmax(int hmax) {
		this.hmax = hmax;
	}

	/**
	 * Getter f�r {@link #table}
	 * 
	 * @return {@link #table}
	 */
	public Card[] getTable() {
		return table;
	}

	/**
	 * Setter f�r {@link #table}
	 * 
	 * @param table
	 *            um {@link #table} zu definieren
	 */
	public void setTable(Card[] table) {
		this.table = table;
	}

	/**
	 * Getter f�r {@link #stopped}
	 * 
	 * @return {@link #stopped}
	 */
	public int getStopped() {
		return stopped;
	}

	/**
	 * Setter f�r {@link #stopped}
	 * 
	 * @param stopped
	 *            um {@link #stopped} zu definieren
	 */
	public void setStopped(int stopped) {
		this.stopped = stopped;
	}

	public void checkMessage(String msg) {
		String[] parts = msg.split(" ");
		if (parts[0] == "swap") {
			swapCards(players[currP].hand, table, Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2]));
			updateTables();
			updateHand();
			currP = nextPlayer(currP);
		} else if (parts[0] == "swapall") {
			for (int i = 0; i < hmax; i++) {
				swapCards(table, players[currP].hand, i, i);
			}
			updateTables();
			updateHand();
			currP = nextPlayer(currP);
		} else if (parts[0] == "close") {
			setStopped(currP);
			currP = nextPlayer(currP);
		} else if (parts[0] == "push") {
			currP = nextPlayer(currP);
		} else if (parts[0] == "choice") {
			choiceResult(Integer.parseInt(parts[1]));
			currP = nextPlayer(dealer);
		} else if (parts[0] == "nextround") {
			int i = Integer.parseInt(parts[1]);
			if (i == 0) {
				for (int j = 0; j < max; j++) {
					players[j].connect("nextround");
				}
				dealer = nextPlayer(dealer);
				startRound();
			}
		}
	}

	private void updateTables() {
		String str = "table";
		for (int i = 0; i < hmax; i++) {
			str = str + " " + table[i].getImage();
		}
		for (int i = 0; i < max; i++) {
			Player p = players[i];
			p.connect(str);
		}
	}

	private void updateHand() {
		updateScore(players[currP]);
		String str = "hand";
		Player p = players[currP];
		for (int i = 0; i < hmax; i++) {
			str = str + " " + p.hand[i].getImage();
		}
		p.connect(str);
	}
	
	private void updateMessage(String msg){
		for (int i = 0; i < max; i++) {
			Player p = players[i];
			p.connect("msg " + msg);
		}
	}

}