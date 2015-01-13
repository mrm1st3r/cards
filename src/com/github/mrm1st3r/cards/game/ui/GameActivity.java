package com.github.mrm1st3r.cards.game.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.util.BitmapUtil;

/**
 * This is the base user interface for both, host and client.
 * 
 * @author Sergius Maier
 * @version 1.0
 */
public abstract class GameActivity extends Activity {

	/**
	 * Default opacity for cards.
	 */
	private static final int FULL_OPACITY = 255;
	/**
	 * Selected opacity for cards.
	 */
	private static final int SELECTED_OPACITY = 150;
	/**
	 * Debug tag.
	 */
	private static final String TAG = GameActivity.class.getSimpleName();
	/**
	 * The card images that are currently shown as hand cards.
	 */
	private String[] hand = new String[ThirtyOne.HAND_SIZE];
	/**
	 * The card images that are currently shown as table cards.
	 */
	private String[] table = new String[ThirtyOne.HAND_SIZE];
	/**
	 * The backside for all cards.
	 */
	private String bg = "card_backside";
	/**
	 * Selected table card to swap.
	 */
	private int checkedTable = -1;
	/**
	 * Selected hand card to swap.
	 */
	private int checkedHand = -1;
	/**
	 * All table card views.
	 */
	private ImageView[] tableCards = new ImageView[ThirtyOne.HAND_SIZE];
	/**
	 * All hand card views.
	 */
	private ImageView[] handCards = new ImageView[ThirtyOne.HAND_SIZE];

	@Override
	protected void onCreate(final Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_game);
		Log.d(TAG, "Creating card lists");
		tableCards[0] = (ImageView) findViewById(R.id.img_table0);
		tableCards[1] = (ImageView) findViewById(R.id.img_table2);
		tableCards[2] = (ImageView) findViewById(R.id.img_table1);
		handCards[0] = (ImageView) findViewById(R.id.img_hand0);
		handCards[1] = (ImageView) findViewById(R.id.img_hand1);
		handCards[2] = (ImageView) findViewById(R.id.img_hand2);
	}

	/**
	 * Process a message that is sent from the game to a player.
	 * 
	 * @param msg
	 *            Message that was sent
	 */
	public final void checkMessage(final String msg) {
		Log.d(TAG, "Check incoming message: " + msg);
		String[] parts = msg.split(" ");
		if (parts[0].equals("active")) {
			active();
		} else if (parts[0].equals("inactive")) {
			inactive();
		} else if (parts[0].equals("lastround")) {
			lastround();
		} else if (parts[0].equals("nextround")) {
			nextRound();
		} else if (parts[0].equals("newgame")) {
			newGameChoice();
		} else if (parts[0].equals("nextroundchoice")) {
			nextRoundChoice();
		} else if (parts[0].equals("takechoice")) {
			takeChoice();
		} else if (parts[0].equals("hand")) {
			hand[0] = parts[1];
			hand[1] = parts[2];
			hand[2] = parts[3];
			showHand();
		} else if (parts[0].equals("table")) {
			table[0] = parts[1];
			table[1] = parts[2];
			table[2] = parts[3];
			showTable();
		} else if (parts[0].equals("msg")) {
			changeText(R.id.lbl_message, msg.substring(4));
		} else if (parts[0].equals("score")) {
			changeText(R.id.lbl_score1, parts[1]);
		} else if (parts[0].equals("life")) {
			changeText(R.id.lbl_life1, parts[1]);
		} else if (parts[0].equals("players")) {
			showPlayers(parts);
		}
	}

	/**
	 * Activates all buttons for an active player.
	 */
	private void active() {
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
	}

	/**
	 * Deactivates all buttons for a player who is waiting.
	 */
	private void inactive() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
	}

	/**
	 * Activates all buttons without the "knock"-button for an active player for
	 * the last round because another player has stopped the round before.
	 */
	private void lastround() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
	}

	/**
	 * Sets everything to default for the next round.
	 */
	private void nextRound() {
		inactive();
		checkedTable = -1;
		checkedHand = -1;
		hand[0] = null;
		hand[1] = null;
		hand[2] = null;
		showHand();
		table[0] = null;
		table[1] = null;
		table[2] = null;
		showTable();
		changeText(R.id.lbl_message, "Neue Runde");
	}

	/**
	 * Gives the host the choice for a new game.
	 */
	private void newGameChoice() {
		alertBox("Ihre Wahl", "Neues Spiel", "Ja", "Nein", "newgame yes",
				"newgame no");
	}

	/**
	 * Gives the host the choice for a new round.
	 */
	private void nextRoundChoice() {
		alertBox("Ihre Wahl", "Nächste Runde", "Ja", "Nein", "nextround yes",
				"nextround no");
	}

	/**
	 * Gives the dealer the choice between the hand cards and the hidden cards
	 * at the beginning of a round.
	 */
	private void takeChoice() {
		showHand();
		alertBox("Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder "
						+ "mit den verdeckten Karten auf dem Tisch spielen",
				"Hand", "Tisch", "choice hand", "choice table");
	}

	/**
	 * Shows the cards on the table.
	 */
	private void showTable() {
		if (table[0] != null) {
			changeImage(R.id.img_table0,
					BitmapUtil.getDrawableIdentifier(this, table[0]));
			changeImage(R.id.img_table1,
					BitmapUtil.getDrawableIdentifier(this, table[1]));
			changeImage(R.id.img_table2,
					BitmapUtil.getDrawableIdentifier(this, table[2]));
		} else {
			changeImage(R.id.img_table0,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(R.id.img_table1,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(R.id.img_table2,
					BitmapUtil.getDrawableIdentifier(this, bg));
		}
	}

	/**
	 * Shows the hand cards of the player.
	 */
	private void showHand() {
		Log.d(TAG, hand[0] + " / " + getPackageName());
		if (hand[0] != null) {
			changeImage(R.id.img_hand0,
					BitmapUtil.getDrawableIdentifier(this, hand[0]));
			changeImage(R.id.img_hand1,
					BitmapUtil.getDrawableIdentifier(this, hand[1]));
			changeImage(R.id.img_hand2,
					BitmapUtil.getDrawableIdentifier(this, hand[2]));
		} else {
			changeImage(R.id.img_hand0,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(R.id.img_hand1,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(R.id.img_hand2,
					BitmapUtil.getDrawableIdentifier(this, bg));
		}
	}

	/**
	 * Show name and image for playing players.
	 * 
	 * @param players
	 *            Message with names of the opponents
	 */
	private void showPlayers(final String[] players) {
		Log.d(TAG, "receiving new player names" + players[1]);
		changeText(R.id.lbl_rival0, players[1]);
		if (players.length >= 3) {
			changeText(R.id.lbl_rival1, players[2]);
			switchImageView(R.id.img_rival1, View.VISIBLE);
			switchTextView(R.id.lbl_rival1, View.VISIBLE);
			if (players.length >= 4) {
				changeText(R.id.lbl_rival2, players[3]);
				switchImageView(R.id.img_rival2, View.VISIBLE);
				switchTextView(R.id.lbl_rival2, View.VISIBLE);
			} else {
				switchImageView(R.id.img_rival2, View.INVISIBLE);
				switchTextView(R.id.lbl_rival2, View.INVISIBLE);
			}
		} else {
			switchImageView(R.id.img_rival1, View.INVISIBLE);
			switchTextView(R.id.lbl_rival1, View.INVISIBLE);
			switchImageView(R.id.img_rival2, View.INVISIBLE);
			switchTextView(R.id.lbl_rival2, View.INVISIBLE);
		}
	}

	/**
	 * A dialog box pops up and waits for the choice of the player.
	 * 
	 * @param title
	 *            Title of the dialog box
	 * @param msg
	 *            Message of the dialog box
	 * @param yes
	 *            Text displayed on the positive button
	 * @param no
	 *            Text displayed on the negative button
	 * @param yesMsg
	 *            Message which will be send to the game if the player clicks
	 *            the positive button
	 * @param noMsg
	 *            Message which will be send to the game if the player clicks
	 *            the negative button
	 */
	private void alertBox(final String title, final String msg,
			final String yes, final String no, final String yesMsg,
			final String noMsg) {
		final Context context = this;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		// set title
		alertDialogBuilder.setTitle(title);
		// set dialog message
		alertDialogBuilder.setMessage(msg).setCancelable(false)
				.setPositiveButton(yes, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						sendMessage(yesMsg);
					}
				}).setNegativeButton(no, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						sendMessage(noMsg);
					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	/**
	 * Displays an image in the ImageView.
	 * 
	 * @param view
	 *            ID of the ImageView which shows the image
	 * @param img
	 *            ID of the image to show
	 */
	private void changeImage(final int view, final int img) {
		ImageView image = (ImageView) findViewById(view);
		image.setImageBitmap(BitmapUtil.decodeSampledBitmapFromResource(
				getResources(), img, image.getWidth(), image.getHeight()));
	}

	/**
	 * Activate or deactivate a button.
	 * 
	 * @param b
	 *            ID of the Button to switch
	 * @param s
	 *            true to activate/ false to deactivate
	 */
	private void switchButton(final int b, final boolean s) {
		Button btn = (Button) findViewById(b);
		btn.setEnabled(s);
	}

	/**
	 * Show or hide an ImageView.
	 * 
	 * @param view
	 *            ID of the Button to switch
	 * @param state
	 *            VISIBLE or INVISIBLE
	 */
	private void switchImageView(final int view, final int state) {
		ImageView iv = (ImageView) findViewById(view);
		iv.setVisibility(state);
	}

	/**
	 * Show or hide an TextView.
	 * 
	 * @param view
	 *            ID of the Button to switch
	 * @param state
	 *            VISIBLE or INVISIBLE
	 */
	private void switchTextView(final int view, final int state) {
		TextView tv = (TextView) findViewById(view);
		tv.setVisibility(state);
	}

	/**
	 * Displays a text in the Textview.
	 * 
	 * @param view
	 *            ID of the TextView which show the text
	 * @param text
	 *            Text to show
	 */
	private void changeText(final int view, final String text) {
		Log.d(TAG, "Changing text to " + text);
		final TextView textViewToChange = (TextView) findViewById(view);
		textViewToChange.setText(text);
	}

	/**
	 * Save the marked card on the table.
	 * 
	 * @param view
	 *            ID of the ImageView which is marked
	 */
	@SuppressWarnings("deprecation")
	public final void img_table(final View view) {
		resetTableCards();
		((ImageView) view).setAlpha(SELECTED_OPACITY);

		switch (view.getId()) {
		case R.id.img_table0:
			checkedTable = 0;
			break;
		case R.id.img_table1:
			checkedTable = 1;
			break;
		case R.id.img_table2:
			checkedTable = 2;
			break;
		default:
			checkedTable = -1;
		}
	}

	/**
	 * Reset all table cards to 100% opacity.
	 */
	@SuppressWarnings("deprecation")
	private void resetTableCards() {
		for (ImageView v : tableCards) {
			if (v == null) {
				Log.d(TAG, "table card fail");
				continue;
			}
			v.setAlpha(FULL_OPACITY);
		}
	}

	/**
	 * Save the marked card in the hand.
	 * 
	 * @param view
	 *            ID of the ImageView which is marked
	 */
	@SuppressWarnings("deprecation")
	public final void img_hand(final View view) {
		resetHandCards();
		((ImageView) view).setAlpha(SELECTED_OPACITY);

		switch (view.getId()) {
		case R.id.img_hand0:
			checkedHand = 0;
			break;
		case R.id.img_hand1:
			checkedHand = 1;
			break;
		case R.id.img_hand2:
			checkedHand = 2;
			break;
		default:
			checkedHand = -1;
		}
	}

	/**
	 * Reset all hand cards to 100% opacity.
	 */
	@SuppressWarnings("deprecation")
	private void resetHandCards() {
		for (ImageView v : handCards) {
			if (v == null) {
				Log.d(TAG, "hand card fail");
				continue;
			}
			v.setAlpha(FULL_OPACITY);
		}
	}

	/**
	 * Send a message to the game, to exchange the marked table card and the
	 * marked hand card.
	 * 
	 * @param view
	 *            Button that was pressed, not used
	 */
	public final void btn_1card(final View view) {
		if (checkedTable != -1 && checkedHand != -1) {
			inactive();
			sendMessage("swap " + checkedHand + " " + checkedTable);
		}
		checkedTable = -1;
		checkedHand = -1;
		resetHandCards();
		resetTableCards();
	}

	/**
	 * Send a message to the game, to exchange the table cards and the hand
	 * cards of the player.
	 * 
	 * @param view
	 *            Button that was pressed, not used
	 */
	public final void btn_allcards(final View view) {
		inactive();
		sendMessage("swapall");
	}

	/**
	 * Send a message to the game, to close the round.
	 * 
	 * @param view
	 *            Button that was pressed, not used
	 */
	public final void btn_knock(final View view) {
		inactive();
		sendMessage("close");
	}

	/**
	 * Send a message to the game, to push.
	 * 
	 * @param view
	 *            Button that was pressed, not used
	 */
	public final void btn_push(final View view) {
		inactive();
		sendMessage("push");
	}

	/**
	 * Send a message from the player to the game logic.
	 * 
	 * @param msg
	 *            Message to send
	 */
	public abstract void sendMessage(String msg);
}
