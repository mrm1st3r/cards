package com.github.mrm1st3r.cards.game;

import java.util.Random;

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

	/**
	 * Konstruktor der Klasse "ThirtyOne".
	 * 
	 * @param m	Anzahl an Spielern
	 */
	public ThirtyOne(int m) {
		super(m, 32);
		setStopped(m+1);
	}

	/**
	 * Ein Dealer wird per Zufall festgelegt und die erste Runde wird gestartet.
	 */
	public void start() {
		Random ran = new Random();
		dealer = ran.nextInt(max);
		startRound();
	}

	/**
	 * Start einer Runde.<br>
	 * Es werden Karten an alle Spieler ausgeteilt.<br>
	 * Der Dealer bekommt die M�glichkeit sich zwischen zwei H�nden zu entscheiden.<br>
	 * Der n�chste wird festgelegt und das Spiel gestartet.
	 */
	public void startRound() {
		Player p;
		for (int i = 0; i < max; i++) {
			if (i != dealer) {
				p = players[i];
				giveHand(p);
			}
		}
		p = players[dealer];
		p.setHand(choice());
		currP = dealer;
		currP = nextPlayer(currP);
		game();
	}

	/**
	 * Spielablauf.<br>
	 * Alle Spieler sind nach einander dran bis einer die Runde beendet.
	 * Danach kommen die anderen Spieler noch mal an die Reihe.
	 */
	private void game() {
		int temp = 0;
		while (stopped > max) {
			//temp = turn();
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
			//lastTurn();
			updateScore(players[currP]);
			currP = nextPlayer(currP);
		} while (currP != stopped);
		endRound();
	}
	
	/**
	 * Ende einer Runde.<br>
	 * Ermittelt den oder die Spieler mit der niedrigsten Punktezahl, die dann ein Leben verlieren.
	 * Entweder gibt es einen Gewinner, Unendschieden oder es werden die Spieler aus dem Spiel genommen,
	 * die keine Leben mehr haben und es wird eine neue Runde gestartet.
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
			//winner();
		} else if (temp == 0) {
			//draw();
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
	 * Dem Dealer wird die M�glichkeit gegeben sich zwischen zwei H�nden zu entscheiden.
	 * Die Hand f�r die er sich nicht entschieden hat wird auf den Tisch gelegt.
	 * 
	 * @return	Hand, f�r die sich der Dealer entschieden hat
	 */
	private Card[] choice() {
		Card[] temp1 = new Card[hmax];
		Card[] temp2 = new Card[hmax];
		for (int i = 0; i < hmax; i++) {
			temp1[i] = takeCard();
			temp2[i] = takeCard();
		}
		/*if (takeChoice(temp1, temp2) == 0) {
			table = temp2;
			return temp1;
		} else {
			table = temp1;
			return temp2;
		}*/
		return temp1;
	}

	/**
	 * N�chster Spieler wird bestimmt.
	 * 
	 * @param p	aktueller Spieler
	 * @return	n�chster Spieler
	 */
	private int nextPlayer(int p) {
		int temp = 0;
		if (p >= max) {
			temp = 0;
		} else {
			temp = p++;
		}
		return temp;
	}

	/**
	 * Es werden {@link hmax} Karten vom Stapel genommen und in die Hand des Spielers hinzugef�gt.
	 * 
	 * @param p	Spieler
	 */
	public void giveHand(Player p) {
		boolean temp = true;
		for (int i = 0; i < hmax; i++) {
			temp = p.add2Hand(takeCard());
			if(temp){
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
	 * @param p Spieler
	 */
	public void updateScore(Player p) {
		Card[] hand = p.getHand();
		p.setScore(calcScore(hand));
	}

	/**
	 * Kalkuliert den Punktestand aus der Hand, die �bergeben wurde.
	 * 
	 * @param c	Hand, von der die Punkte ermittelt werden soll
	 * @return	kalkulierte Punktezahl
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
		}
		result = Math.max(hearts, diamonds);
		result = Math.max(result, spades);
		result = Math.max(result, clover);
		return Math.max(result, same);
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
	 * @param hmax um {@link #hmax} zu definieren
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
	 * @param table um {@link #table} zu definieren
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
	 * @param stopped um {@link #stopped} zu definieren
	 */
	public void setStopped(int stopped) {
		this.stopped = stopped;
	}
}