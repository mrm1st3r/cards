package com.github.mrm1st3r.cards.game;

import java.util.Random;

/**
 * Grundgerüst für alle Kartenspiele der App.
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public class Gameplay {

	/**
	 * Array mit allen aktiven Spielern
	 */
	Player[] players;
	/**
	 * Deck/ Stapel
	 */
	Card[] cards;
	/**
	 * Index des Spielers, der an der Reihe ist
	 */
	int currP;
	/**
	 * Anzahl der aktiven Spieler
	 */
	int max;
	/**
	 * Index der obersten Karte auf dem Deck/ Stapel
	 */
	int last;
	/**
	 * Index des Kartengebers
	 */
	int dealer;

	/**
	 * Konstruktor der Klasse "Gameplay".<br>
	 * Die Anzahl der aktiven Spieler wird festgelegt.<br>
	 * Ein Kartendeck wird erstellt und durchgemischt.
	 * 
	 * @param m
	 *            Anzahl teilnehmender Spieler
	 * @param n
	 *            Anzahl Karten
	 */
	public Gameplay(int m, int n) {
		setMax(m);
		setPlayers(new Player[max]);
		createCards(n);
		mixCards(cards);
	}

	/**
	 * Ein Kartendeck wird erstellt.
	 * 
	 * @param m
	 *            Anzahl der Karten im Deck
	 */
	public void createCards(int m) {
		last = m - 1;
		int index = m / 4;
		cards = new Card[m];
		for (int i = 0; i < 4; i++) {
			int s = 14;
			Card c = null;
			String name = "";
			int value = 0;
			for (int j = index; j > 0; j--) {
				switch (s) {
				case 14:
					name = "ace";
					value = 11;
					s = 13;
					break;
				case 13:
					name = "king";
					value = 10;
					s = 12;
					break;
				case 12:
					name = "queen";
					s = 11;
					break;
				case 11:
					name = "jack";
					s = 10;
					break;
				case 10:
					name = "10";
					s = 9;
					break;
				case 9:
					name = "9";
					value = 9;
					s = 8;
					break;
				case 8:
					name = "8";
					value = 8;
					s = 7;
					break;
				case 7:
					name = "7";
					value = 7;
					s = 6;
					break;
				case 6:
					name = "6";
					value = 6;
					s = 5;
					break;
				case 5:
					name = "5";
					value = 5;
					s = 4;
					break;
				case 4:
					name = "4";
					value = 4;
					s = 3;
					break;
				case 3:
					name = "3";
					value = 3;
					s = 2;
					break;
				case 2:
					name = "2";
					value = 2;
					s = 1;
					break;
				}
				c = new Card(name, i, value);
				cards[i * index + j - 1] = c;
			}
		}
	}

	/**
	 * Mischt ein Kartenstapel durch.
	 * 
	 * @param c
	 *            Array mit Karten (Deck/ Stapel)
	 */
	public static void mixCards(Card[] c) {
		int n = c.length;
		Random random = new Random();
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swapCards(c, i, change);
		}
	}

	/**
	 * Vertauscht 2 Karten eines Kartenstapels mit einander.
	 * 
	 * @param c
	 *            Kartenstapel
	 * @param i
	 *            Index der 1. Karte
	 * @param change
	 *            Index der 2. Karte
	 */
	private static void swapCards(Card[] c, int i, int change) {
		Card helper = c[i];
		c[i] = c[change];
		c[change] = helper;
	}

	/**
	 * Die i-te Karte wird aus dem Deck entfernt.
	 * 
	 * @param i
	 *            Index der zuentfernenden Karte
	 */
	public void removeCard(int i) {
		cards[i] = null;
	}

	/**
	 * Die oberste Karte vom Deck/ Stapel wird entfernt und zurückgegegeben.
	 * 
	 * @return oberste Karte des Decks/ Stapels
	 */
	public Card takeCard() {
		if (cards[last] != null) {
			Card temp = cards[last];
			removeCard(last);
			last--;
			return temp;
		} else {
			return null;
		}
	}

	/**
	 * Gibt die Karten des Decks/Stapels auf der Konsole aus.
	 */
	public void printCards() {
		int i = 1;
		for (Card c : cards) {
			System.out.print(i + " " + c.toString() + "\n");
			i++;
		}
	}

	/**
	 * Fügt einen Spieler hinzu.
	 * 
	 * @param p
	 *            hinzuzufügender Spieler
	 * @return True/False, je nachdem, ob das Einfügen funktioniert hat oder
	 *         nicht
	 */
	public boolean addPlayer(Player p) {
		int i = 0;
		while (players[i] != null && i < max - 1) {
			i++;
		}
		if (players[i] != null) {
			players[i] = p;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Getter für {@link #players}
	 * 
	 * @return {@link #players}
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Setter für {@link #players}
	 * 
	 * @param players
	 *            um {@link #players} zu definieren
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * Getter für {@link #currP}
	 * 
	 * @return {@link #currP}
	 */
	public int getCurrP() {
		return currP;
	}

	/**
	 * Setter für {@link #currP}
	 * 
	 * @param currP
	 *            um {@link #currP} zu definieren
	 */
	public void setCurrP(int currP) {
		this.currP = currP;
	}

	/**
	 * Getter für {@link #max}
	 * 
	 * @return {@link #max}
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Setter für {@link #max}
	 * 
	 * @param max
	 *            um {@link #max} zu definieren
	 */
	private void setMax(int max) {
		this.max = max;
	}
}