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

public abstract class GameActivity extends Activity {

	private static final String TAG = GameActivity.class.getSimpleName();
	
	String[] hand = new String[3];
	String[] table = new String[3];
	String bg = "card_backside";
	String checkedTable = null;
	String checkedHand = null;

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
	}

	public void checkMessage(String msg) {
		
		Log.d(TAG, "Check incoming message: " + msg);
		
		String[] parts = msg.split(" ");
		if (parts[0] == "active") {
			active();
		} else if (parts[0] == "inactive") {
			inactive();
		} else if (parts[0] == "lastround") {
			lastround();
		} else if (parts[0] == "nextround") {
			nextRound();
		} else if (parts[0] == "newgame") {
			newGameChoice();
		} else if (parts[0] == "nextroundchoice") {
			nextRoundChoice();
		} else if (parts[0] == "takechoice") {
			takeChoice();
		} else if (parts[0] == "hand") {
			hand[0] = parts[1];
			hand[1] = parts[2];
			hand[2] = parts[3];
			showHand();
		} else if (parts[0] == "table") {
			table[0] = parts[1];
			table[1] = parts[2];
			table[2] = parts[3];
			showTable();
		} else if (parts[0] == "msg") {
			String str = "";
			for(String s: parts){
				if (s != "msg"){
					str = str + s;
				}
			}
			changeText(R.id.lbl_message, str);
		} else if (parts[0] == "score") {
			changeText(R.id.lbl_score1, parts[1]);
		} else if (parts[0] == "life") {
			changeText(R.id.lbl_life1, parts[1]);
		} else if (parts[0] == "players") {
			changeText(R.id.lbl_rival0, parts[1]);
			if (parts[2] != null)
				changeText(R.id.lbl_rival1, parts[2]);
			if (parts[3] != null)
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
		inactive();
		table = null;
		hand = null;
		showTable();
		showHand();
	}

	private void newGameChoice() {
		alertBox("Ihre Wahl", "Neues Spiel", "Ja", "Nein", "nextround yes",
				"nextround no");
	}
	
	private void nextRoundChoice() {
		alertBox("Ihre Wahl", "Nächste Runde", "Ja", "Nein", "newgame yes",
				"newgame no");
	}

	private void takeChoice() {
		showHand();
		alertBox(
				"Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder mit den verdeckten Karten auf dem Tisch spielen",
				"Hand", "Tisch", "choise hand", "choice table");
	}

	private void showTable() {
		if (table != null) {
			changeImage(
					R.id.img_table0,
					getResources().getIdentifier(table[0], "drawable",
							getPackageName()));
			changeImage(
					R.id.img_table1,
					getResources().getIdentifier(table[1], "drawable",
							getPackageName()));
			changeImage(
					R.id.img_table2,
					getResources().getIdentifier(table[2], "drawable",
							getPackageName()));
		} else {
			changeImage(
					R.id.img_table0,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
			changeImage(
					R.id.img_table1,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
			changeImage(
					R.id.img_table2,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
		}
	}

	private void showHand() {
		if (hand != null) {
			changeImage(
					R.id.img_hand0,
					getResources().getIdentifier(hand[0], "drawable",
							getPackageName()));
			changeImage(
					R.id.img_hand1,
					getResources().getIdentifier(hand[1], "drawable",
							getPackageName()));
			changeImage(
					R.id.img_hand2,
					getResources().getIdentifier(hand[2], "drawable",
							getPackageName()));
		} else {
			changeImage(
					R.id.img_hand0,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
			changeImage(
					R.id.img_hand1,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
			changeImage(
					R.id.img_hand2,
					getResources().getIdentifier(bg, "drawable",
							getPackageName()));
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
		image.setImageResource(img);
	}

	private void switchButton(int b, boolean s) {
		Button btn = (Button) findViewById(b);
		btn.setEnabled(s);
	}

	private void changeText(int view, String text) {
		final TextView textViewToChange = (TextView) findViewById(view);
		textViewToChange.setText(text);
	}

	public void img_table(View view) {
		switch (view.getId()) {
		case R.id.img_table0:
			checkedTable = table[0];
			break;
		case R.id.img_table1:
			checkedTable = table[1];
			break;
		case R.id.img_table2:
			checkedTable = table[2];
			break;
		}
	}

	public void img_hand(View view) {
		switch (view.getId()) {
		case R.id.img_hand0:
			checkedHand = hand[0];
			break;
		case R.id.img_hand1:
			checkedHand = hand[1];
			break;
		case R.id.img_hand2:
			checkedHand = hand[2];
			break;
		}
	}

	public void btn_1card(View view) {
		if (checkedTable != null && checkedHand != null) {
			inactive();
			sendMessage("swap " + checkedHand + " " + checkedTable);
		}
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

	public abstract void sendMessage(String msg);
	public abstract void newGame();
}