package com.github.mrm1st3r.cards.game;

import java.util.LinkedList;

import android.util.Log;

/**
 * This class describes the game rules and sequence for the game "Thirty-One".
 */
public class ThirtyOne extends CardGame {

	public static final int MAX_LIFES = 3;
	public static final int HAND_SIZE = 3;

	private static final String TAG = ThirtyOne.class.getSimpleName();

	private static final int POINTS_MAX = 33;
	private static final float POINTS_TRIPLE = 30.5f;
	private static final int POINTS_WIN = 31;

	private Player mStopper;
	private Player mWinner;
	private CardDeck mChoice;

	public ThirtyOne(int pPlayerCount) {
		super(pPlayerCount, CardValue.getSkatDeck());
	}

	@Override
	protected void onStart() {
		mStopper = null;
		mWinner = null;
		mChoice = new CardDeck();
		setDealer(getHostPlayer());
	}

	@Override
	protected void onBeforePlay() {
		createCardDeck();
		for (Player p : getPlayers()) {
			if (p.getLifesLeft() < 0) {
				continue;
			}
			fillHand(p.getHand());
			p.command(Command.encode("hand", p.getHand().getImages()));
			updateStatus(p);
		}
		broadcast(Command.encode("msg", "Der Dealer ist dabei sich zu entscheiden"));
		fillHand(mChoice);
		getDealer().command("takechoice");
		synchronized (getDealer().getLock()) {
			try {
				getDealer().getLock().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPlay() {
		Player p = getCurrentPlayer();
		while (!p.equals(mStopper) && mWinner == null) {
			broadcast(Command.encode("msg", p.getName() + " ist an der Reihe"));
			p.command("active");
			synchronized (p.getLock()) {
				try {
					p.getLock().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			p.setScore(calculateScore(p.getHand()));
			updateStatus(p);
			if (p.getScore() >= POINTS_WIN) {
				mWinner = p;
			}
			p = nextPlayerFor(p);
			setCurrentPlayer(p);
			if (p == null) {
				endGame();
				throw new IllegalStateException("Game with less than two players");
			}
		}
	}

	@Override
	protected void onAfterPlay() {
		float minScore = POINTS_MAX;
		float maxScore = 0;
		LinkedList<Player> losers = new LinkedList<Player>();
		for (Player p : getPlayers()) {
			if (p.getLifesLeft() >= 0) {
				minScore = Math.min(minScore, p.getScore());
				maxScore = Math.max(maxScore, p.getScore());
			}
		}
		decreaseLoserLife(minScore, losers);
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

	private void decreaseLoserLife(float minScore, LinkedList<Player> losers) {
		for (Player p : getPlayers()) {
			if (p.getLifesLeft() >= 0 && p.getScore() == minScore) {
				p.decreaseLifes();
			}
			if (p.getScore() == minScore) {
				losers.add(p);
			}
		}
	}

	@Override
	protected void onEnd() {
		if (mWinner != null) {
			broadcast(Command.encode("msg", mWinner.getName() + " hat das Spiel gewonnen"));
		} else {
			Log.d(TAG, "All players left");
		}
	}

	@Override
	public void handleMessage(Player p, String msg) {
		Log.d(TAG, "game receiving: " + msg);
		if (msg.length() == 0) {
			Log.d(TAG, "received empty message from" + p.getName());
			return;
		}
		Command comm = new Command(msg);
		if (comm.getCommand().equals("left") && p.getName().equals(comm.getArg(0))) {
			getPlayers().remove(p);
			broadcast(msg);
		}
		if (p == getCurrentPlayer()) {
			String command = comm.getCommand();
			switch (command) {
				case "swap":
					int handPos = Integer.parseInt(comm.getArg(0));
					int tablePos = Integer.parseInt(comm.getArg(1));
					Card handCard = p.getHand().getCard(handPos);
					Card tableCard = getTableCards().getCard(tablePos);
					p.getHand().replaceCard(handCard, tableCard);
					getTableCards().replaceCard(tableCard, handCard);
					updateTable();
					updateHand(p);
					break;
				case "swapall":
					p.getHand().swapWith(getTableCards());
					updateTable();
					updateHand(p);
					break;
				case "close":
					if (mStopper == null) {
						mStopper = p;
					}
					break;
				case "push":
					Log.i(TAG, p.getName() + " has pushed");
					break;
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

	private void fillHand(CardDeck d) {
		d.reset();
		for (int i = 0; i < HAND_SIZE; i++) {
			d.addCard(takeCardFromDeck());
		}
	}

	private int countLivingPlayers() {
		int num = 0;
		for (Player p : getPlayers()) {
			if (p.getLifesLeft() >= 0) {
				num++;
			}
		}
		return num;
	}

	private float calculateScore(CardDeck d) {
		float hearts = 0;
		float diamonds = 0;
		float spades = 0;
		float clubs = 0;
		float score = 0;
		final Card[] hand = d.getAll();
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

	private void choiceResult(String str) {
		if (str.equals("table")) {
			getDealer().getHand().swapWith(mChoice);
			updateHand(getDealer());
		}
		getTableCards().replaceDeck(mChoice);
		updateTable();
	}

	private void updateTable() {
		broadcast(Command.encode("table", getTableCards().getImages()));
	}

	private void updateHand(Player p) {
		p.command(Command.encode("hand", p.getHand().getImages()));
	}

	private void updateStatus(Player p) {
		String status;
		if (p.getLifesLeft() < 0) {
			status = "Ausgeschieden";
		} else {
			status = p.getScore() + " Punkte, " + p.getLifesLeft() + " Leben";
		}
		p.command(Command.encode("status", status));
	}
}
