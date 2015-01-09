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
		tableCards[1] = (ImageView) findViewById(R.id.img_table1);
		tableCards[2] = (ImageView) findViewById(R.id.img_table2);
		

		handCards[0] = (ImageView) findViewById(R.id.img_hand0);
		handCards[1] = (ImageView) findViewById(R.id.img_hand1);
		handCards[2] = (ImageView) findViewById(R.id.img_hand2);
	}
	
	/**
	 * Process a message that is sent from the game to a player.
	 * @param msg Message that was sent
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
			Log.d(TAG, "receiving new player names" + parts[1]);
			changeText(R.id.lbl_rival0, parts[1]);
			if (parts.length >= 3)
				changeText(R.id.lbl_rival1, parts[2]);
			if (parts.length >= 4)
				changeText(R.id.lbl_rival2, parts[3]);
		}
	}

	private void active() {
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
	}

	private void inactive() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
	}
	
	private void lastround() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
	}

	private void nextRound() {
		/*inactive();
		table = null;
		hand = null;
		showTable();
		showHand();*/
	}

	private void newGameChoice() {
		alertBox("Ihre Wahl", "Neues Spiel", "Ja", "Nein", "newgame yes",
				"newgame no");
	}
	
	private void nextRoundChoice() {
		alertBox("Ihre Wahl", "Nächste Runde", "Ja", "Nein", "nextround yes",
				"nextround no");
	}

	private void takeChoice() {
		showHand();
		alertBox(
				"Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder mit den verdeckten Karten auf dem Tisch spielen",
				"Hand", "Tisch", "choice hand", "choice table");
	}

	private void showTable() {
		if (table != null) {
			changeImage(
					R.id.img_table0,
					BitmapUtil.getDrawableIdentifier(this, table[0]));
			changeImage(
					R.id.img_table1,
					BitmapUtil.getDrawableIdentifier(this, table[1]));
			changeImage(
					R.id.img_table2,
					BitmapUtil.getDrawableIdentifier(this, table[2]));
		} else {
			changeImage(
					R.id.img_table0,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(
					R.id.img_table1,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(
					R.id.img_table2,
					BitmapUtil.getDrawableIdentifier(this, bg));
		}
	}

	private void showHand() {
		Log.d(TAG, hand[0] + " / " + getPackageName());
		if (hand[0] != null) {
			changeImage(
					R.id.img_hand0,
					BitmapUtil.getDrawableIdentifier(this, hand[0]));
			changeImage(
					R.id.img_hand1,
					BitmapUtil.getDrawableIdentifier(this, hand[1]));
			changeImage(
					R.id.img_hand2,
					BitmapUtil.getDrawableIdentifier(this, hand[2]));
		} else {
			changeImage(
					R.id.img_hand0,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(
					R.id.img_hand1,
					BitmapUtil.getDrawableIdentifier(this, bg));
			changeImage(
					R.id.img_hand2,
					BitmapUtil.getDrawableIdentifier(this, bg));
		}
	}

	private void alertBox(String title, String msg, String yes, String no,
			final String yesMsg, final String noMsg) {
		final Context context = this;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(msg).setCancelable(false)
				.setPositiveButton(yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						sendMessage(yesMsg);
					}
				}).setNegativeButton(no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						sendMessage(noMsg);
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void changeImage(int view, int img) {
		ImageView image = (ImageView) findViewById(view);
		//image.setImageResource(img);
		image.setImageBitmap(BitmapUtil.decodeSampledBitmapFromResource(
				getResources(), img, image.getWidth(), image.getHeight()));
	}

	private void switchButton(int b, boolean s) {
		Button btn = (Button) findViewById(b);
		btn.setEnabled(s);
	}

	private void changeText(int view, String text) {
		Log.d(TAG, "Changing text to" + text);
		final TextView textViewToChange = (TextView) findViewById(view);
		textViewToChange.setText(text);
	}

	public void img_table(View view) {
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

	public void img_hand(View view) {
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

	public void btn_1card(View view) {
		if (checkedTable != -1 && checkedHand != -1) {
			inactive();
			sendMessage("swap " + checkedHand + " " + checkedTable);
		}
		checkedTable = -1;
		checkedHand = -1;
		resetHandCards();
		resetTableCards();
	}

	public void btn_allcards(View view) {
		inactive();
		sendMessage("swapall");
	}

	public void btn_knock(View view) {
		inactive();
		sendMessage("close");
	}

	public void btn_push(View view) {
		inactive();
		sendMessage("push");
	}

	/**
	 * Send a message from the player to the game logic.
	 * @param msg Message to send
	 */
	public abstract void sendMessage(String msg);
}
