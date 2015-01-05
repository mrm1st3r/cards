package com.github.mrm1st3r.cards.ingame;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {

	String[] hand = new String[3];
	String[] table = new String[3];

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.activity_game);
	}

	public void checkMessage(String msg) {
		if (msg == "active") {
			// active();
		}

	}

	public void waiting(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards();
	}

	public void out(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards();
	}

	public void active(Bundle bun) {
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, true);
		showCards();
	}

	public void takeChoice(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		Button b = (Button) findViewById(R.id.btn_1card);
		b.setText("Hand");
		switchButton(R.id.btn_1card, true);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		b = (Button) findViewById(R.id.btn_allcards);
		b.setText("Tisch");
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards();

		b = (Button) findViewById(R.id.btn_1card);
		b.setText("1 Karte");
		b = (Button) findViewById(R.id.btn_allcards);
		b.setText("Alle Karten");
	}

	public void showCards() {
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
		}
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
		}
	}

	private void changeImage(int view, int img) {
		ImageView image = (ImageView) findViewById(view);
		image.setImageResource(img);
	}

	private void switchButton(int b, boolean s) {
		Button btn = (Button) findViewById(b);
		btn.setEnabled(s);
	}

	public void btn_31() {
		// Card[] temp = hand;
	}

	public void btn_choice(int i) {

	}

}
