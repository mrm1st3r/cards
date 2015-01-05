package com.github.mrm1stt3r.cards.ingame;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.Card;
import com.github.mrm1st3r.cards.game.Player;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);

		Bundle bundle = getIntent().getExtras();
		Player[] players = (Player[]) bundle.get("players");
		int me = bundle.getInt("me");
		int max = bundle.getInt("max");
		int i = nextP(me, max, me);
		int temp = 2;
		TextView textViewToChange;
		switch (temp) {
		case 2:
			textViewToChange = (TextView) findViewById(R.id.lbl_rival0);
			textViewToChange.setText(players[i].getName());
			if (temp == max) {
				break;
			}
			i = nextP(i, max, me);
		case 3:
			textViewToChange = (TextView) findViewById(R.id.lbl_rival1);
			textViewToChange.setText(players[i].getName());
			if (temp == max) {
				break;
			}
			i = nextP(i, max, me);
		case 4:
			textViewToChange = (TextView) findViewById(R.id.lbl_rival2);
			textViewToChange.setText(players[i].getName());
			break;
		}
	}

	private int nextP(int i, int max, int me) {
		if (i == max) {
			i = 0;
		} else {
			i++;
		}
		if (i == me) {
			i = nextP(i, max, me);
		}
		return i;
	}

	public void waiting(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards(bun);
	}

	public void out(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards(bun);
	}

	public void active(Bundle bun) {
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, true);
		showCards(bun);
	}

	public void takeChoice(Bundle bun) {
		switchButton(R.id.btn_knock, false);
		Button b = (Button)findViewById(R.id.btn_1card);
		b.setText("Hand");
		switchButton(R.id.btn_1card, true);
		b.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}});
		b = (Button)findViewById(R.id.btn_allcards);
		b.setText("Tisch");
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards(bun);
		
		b = (Button)findViewById(R.id.btn_1card);
		b.setText("1 Karte");
		switchButton(R.id.btn_1card, true);
		b = (Button)findViewById(R.id.btn_allcards);
		b.setText("Alle Karten");
	}

	public void showCards(Bundle bun) {
		Card[] table = (Card[]) bun.get("table");
		Card[] hand = (Card[]) bun.get("hand");
		changeImage(
				R.id.img_table0,
				getResources().getIdentifier(table[0].getImage(), "drawable",
						getPackageName()));
		changeImage(
				R.id.img_table1,
				getResources().getIdentifier(table[1].getImage(), "drawable",
						getPackageName()));
		changeImage(
				R.id.img_table2,
				getResources().getIdentifier(table[2].getImage(), "drawable",
						getPackageName()));
		if (hand != null) {
			changeImage(
					R.id.img_hand0,
					getResources().getIdentifier(hand[0].getImage(),
							"drawable", getPackageName()));
			changeImage(
					R.id.img_hand1,
					getResources().getIdentifier(hand[1].getImage(),
							"drawable", getPackageName()));
			changeImage(
					R.id.img_hand2,
					getResources().getIdentifier(hand[2].getImage(),
							"drawable", getPackageName()));
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
		//Card[] temp = hand;
	}

	public void btn_choice(int i){
		
	}
	
}
