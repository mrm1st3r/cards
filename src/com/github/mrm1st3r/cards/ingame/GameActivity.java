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
		} else if (msg == "wait"){
			
		} else if (msg == "lost"){
			
		} else if (msg == "takechoice"){
			
		} else if (msg == "message"){
			
		}
		
		// msg splitten für message hand table

	}

	public void waiting() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		showCards();
	}

	public void out() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		showCards();
	}

	public void active() {
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
		showCards();
	}

	public void takeChoice() {
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, false);
		showCards();
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

	public void btn_1card() {
		
	}

	public void btn_allcards() {

	}
	
	public void btn_knock(){
		
	}
	
	public void btn_push(){
		
	}
}