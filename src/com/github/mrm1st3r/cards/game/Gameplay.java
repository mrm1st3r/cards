package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

/**
 * Grundger�st f�r alle Kartenspiele der App.
 * 
 * @author Sergius Maier
 * @version 0.8
 */
public abstract class Gameplay {

	public final Object playerLock = new Object();

	/**
	 * Array mit allen aktiven Spielern.
	 */
	protected Player[] players;
	/**
	 * Deck/ Stapel.
	 */
	private LinkedList<Card> cards;
	
	private CardValue[] cardDeckType;
	/**
	 * Index des Spielers, der an der Reihe ist.
	 */
	protected int currP;
	/**
	 * Anzahl der aktiven Spieler.
	 */
	protected int playerCount;

	/**
	 * Index des Kartengebers.
	 */
	protected int dealer;

	/**
	 * Konstruktor der Klasse "Gameplay".<br>
	 * Die Anzahl der aktiven Spieler wird festgelegt.<br>
	 * Ein Kartendeck wird erstellt und durchgemischt.
	 * 
	 * @param pPlayerCount Anzahl teilnehmender Spieler
	 * @param pCardDeck Anzahl Karten
	 */
	public Gameplay(final int pPlayerCount, final CardValue[] pCardDeck) {
		playerCount = pPlayerCount;
		cardDeckType = pCardDeck;
		players = new Player[playerCount];
	}

	/**
	 * Create a new card deck.
	 * @param values Card values to use
	 */
	protected final void createCardDeck() {
		
		cards = new LinkedList<Card>();
		
		for (CardColor color : CardColor.values()) {
			for (CardValue value : cardDeckType) {
				cards.add(new Card(color, value));
			}
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
	protected static void swapCards(Card[] one, Card[] two, int i, int change) {
		Card helper = one[i];
		one[i] = two[change];
		two[change] = helper;
	}


	/**
	 * Die oberste Karte vom Deck/ Stapel wird entfernt und zur�ckgegegeben.
	 * 
	 * @return oberste Karte des Decks/ Stapels
	 */
	public final Card takeCard() {
		if (cards.size() == 0) {
			return null;
		}
		
		int num = (int) Math.random() * cards.size();
		Card c = cards.get(num);
		cards.remove(num);
		
		return c;
	}

	/**
	 * F�gt einen Spieler hinzu.
	 * 
	 * @param p
	 *            hinzuzuf�gender Spieler
	 * @return True/False, je nachdem, ob das Einf�gen funktioniert hat oder
	 *         nicht
	 */
	public boolean addPlayer(Player p) {
		int i = 0;
		while (players[i] != null && i < playerCount - 1) {
			i++;
		}
		if (players[i] == null) {
			players[i] = p;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Getter f�r {@link #players}
	 * 
	 * @return {@link #players}
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Setter f�r {@link #players}
	 * 
	 * @param players
	 *            um {@link #players} zu definieren
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * Getter f�r {@link #currP}
	 * 
	 * @return {@link #currP}
	 */
	public int getCurrP() {
		return currP;
	}

	/**
	 * Setter f�r {@link #currP}
	 * 
	 * @param currP
	 *            um {@link #currP} zu definieren
	 */
	public void setCurrP(int currP) {
		this.currP = currP;
	}

	/**
	 * Getter f�r {@link #playerCount}
	 * 
	 * @return {@link #playerCount}
	 */
	public int getMax() {
		return playerCount;
	}
	
	public abstract void checkMessage(String msg);
}