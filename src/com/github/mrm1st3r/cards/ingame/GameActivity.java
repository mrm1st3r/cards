package com.github.mrm1st3r.cards.ingame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mrm1st3r.cards.R;

public abstract class GameActivity extends Activity {

	String[] hand = new String[3];
	String[] table = new String[3];
	String bg = "card_backside";
	String checkedTable = null;
	View vTable = null;
	String checkedHand = null;
	View vHand = null;

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
	}

	public void checkMessage(String msg) {
		String[] parts = msg.split(" ");
		if (parts[0] == "active") {
			active();
		} else if (parts[0] == "inactive") {
			inactive();
		} else if (parts[0] == "lost") {
			lost();
		} else if (parts[0] == "nextround") {
			nextRound();
		} else if (parts[0] == "takechoice") {
			takeChoice(parts[1], parts[2], parts[3]);
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
			changeText(R.id.lbl_message, parts[1]);
		} else if (parts[0] == "score") {
			changeText(R.id.lbl_score1, parts[1]);
		} else if (parts[0] == "life") {
			changeText(R.id.lbl_life1, parts[1]);
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

	private void lost() {
		inactive();
		table = null;
		hand = null;
		showTable();
	}
	
	private void nextRound(){
		alertBox(
				"Ihre Wahl",
				"Nächste Runde",
				"Ja", "Nein", "nextround 0", "nextround 1");
	}

	private void takeChoice(String str0, String str1, String str2) {
		hand[0] = str0;
		hand[1] = str1;
		hand[2] = str2;
		showHand();
		alertBox(
				"Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder mit den verdeckten Karten auf dem Tisch spielen",
				"Hand", "Tisch", "choise 0", "choice 1");
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

	private void alertBox(String title, String msg, String yes, String no, final String yesMsg, final String noMsg) {
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
	
	private void changeText(int view, String text){
		final TextView textViewToChange = (TextView) findViewById(view);
		textViewToChange.setText(text);
	}
	
	public void img_table(View view) {
		String resName = getResourceNameFromClassByID(R.drawable.class, R.drawable.imagename);

	}

	public void img_hand(View view) {
		sendMessage("swapall");
	}

	public void btn_1card(View view) {
		
	}

	public void btn_allcards(View view) {
		sendMessage("swapall");
	}

	public void btn_knock(View view) {
		sendMessage("close");
	}

	public void btn_push(View view) {
		sendMessage("push");
	}
	
	public abstract void sendMessage(String msg);
}