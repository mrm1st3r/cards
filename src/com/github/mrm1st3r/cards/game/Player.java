package com.github.mrm1st3r.cards.game;

import com.github.mrm1st3r.btutil.AsyncBluetoothConnection;

/**
 * Kartenspieler
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public abstract class Player {

	/**
	 * Spielername
	 */
	String name;
	/**
	 * Spielerleben
	 */
	int life = 0;
	/**
	 * aktuelle Punkte des Spielers
	 */
	float score = 0;
	/**
	 * max. Anzahl von Karten die ein Spieler in der Hand haben kann
	 */
	int max;
	/**
	 * Array mit den Karten des Spielers
	 */
	Card[] hand;
	
	Gameplay game;

	/**
	 * Konstruktor der Klasse "Player".
	 * 
	 * @param n		Name des Spielers
	 * @param m		max. Anzahl Karten, die der Spieler in der Hand haben kann
	 * @param conn	Verbindung zum Client
	 */
	public Player(String n, int m) {
		setName(n);
		setMax(m);
		setHand(new Card[max]);
	}

	/**
	 * Ein freier Platz in der Hand des Spieler wird gesucht.<br>
	 * Anschlie�end wird die Karte "c" an diese Stelle eingef�gt.
	 * 
	 * @param c	zueinf�gende Karte
	 * @return	True/False, je nachdem, ob das Einf�gen funktioniert hat oder nicht
	 */
	public boolean add2Hand(Card c) {
		int i = 0;
		while (hand[i] != null && i < max - 1) {
			i++;
		}
		if (hand[i] != null) {
			insertIntoHand(i, c);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Karte "c" wird an der i-ten Stelle in die Hand des Spielers eingef�gt.
	 * 
	 * @param i	Index f�r die Stelle in der Hand
	 * @param c	zueinf�gende Karte
	 */
	private void insertIntoHand(int i, Card c) {
		hand[i] = c;
	}

	/**
	 * Entfernt die Karte an der i-ten Stelle der Hand des Spielers.
	 * 
	 * @param i	Index f�r die Stelle in der Hand
	 */
	public void removeFromHand(int i) {
		hand[i] = null;
	}
	
	/**
	 * Baut die Verbindung zum Bluetoothger�t auf und sendet eine Nachricht.
	 */
	public abstract void connect(String msg);

	/**
	 * Getter f�r {@link #name}
	 * 
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter f�r {@link #name}
	 * 
	 * @param name um {@link #name} zu definieren
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter f�r {@link #score}
	 * 
	 * @return {@link #score}
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Setter f�r {@link #score}
	 * 
	 * @param score um {@link #score} zu definieren
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * Getter f�r {@link #max}
	 * 
	 * @return {@link #max}
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Setter f�r {@link #max}
	 * 
	 * @param max um {@link #max} zu definieren
	 */
	private void setMax(int max) {
		this.max = max;
	}

	/**
	 * Getter f�r {@link #hand}
	 * 
	 * @return {@link #hand}
	 */
	public Card[] getHand() {
		return hand;
	}

	/**
	 * Setter f�r {@link #hand}
	 * 
	 * @param hand um {@link #hand} zu definieren
	 */
	public void setHand(Card[] hand) {
		this.hand = hand;
	}

	/**
	 * Getter f�r {@link #life}
	 * 
	 * @return {@link #life}
	 */
	public int getLife() {
		return life;
	}

	/**
	 * Setter f�r {@link #life}
	 * 
	 * @param life um {@link #life} zu definieren
	 */
	public void setLife(int life) {
		this.life = life;
	}
	
	/**
	 * Verringert das Leben um eins.
	 */
	public void decreaseLife() {
		this.life--;
	}
	
	public void setGame(Gameplay g) {
		game = g;
	}

	@Override
	public String toString() {
		return name + " hat " + score + " Punkte";
	}
}