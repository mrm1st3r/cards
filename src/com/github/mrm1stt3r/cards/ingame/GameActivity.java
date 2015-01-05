package com.github.mrm1stt3r.cards.ingame;

import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.Card;
import com.github.mrm1st3r.cards.game.Player;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle bun) {
		super.onCreate(bun);
		
		Bundle bundle = getIntent().getExtras();
		Player[] players = (Player[])bundle.get("players");
		int me = bundle.getInt("me");
		int max = bundle.getInt("max");
		int i;
		if(me == max){
			i = 0;
		} else {
			i = me++;
		}
		while(i != me){
			Text image = (ImageView) findViewById(view);
			image.setImageResource(img);
			if(i == max){
				i = 0;
			} else {
				i ++;
			}
		}
	}

	public void waiting(Bundle bun){
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, false);
		switchButton(R.id.btn_allcards, false);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards(bun);
	}
	
	public void active(Bundle bun){
		switchButton(R.id.btn_knock, true);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, true);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, true);
		showCards(bun);
	}
	
	public void takeChoice(Bundle bun){
		switchButton(R.id.btn_knock, false);
		switchButton(R.id.btn_1card, true);
		switchButton(R.id.btn_allcards, true);
		switchButton(R.id.btn_push, false);
		switchButton(R.id.btn_nextround, false);
		switchButton(R.id.btn_31, false);
		showCards(bun);
	}
	
	public void showCards(Bundle bun){
		Card[] table = (Card[]) bun.get("table");
		Card[] hand = (Card[]) bun.get("hand");
		changeImage(R.id.img_table0, getResources().getIdentifier(table[0].getImage(), "drawable", getPackageName()));
		changeImage(R.id.img_table1, getResources().getIdentifier(table[1].getImage(), "drawable", getPackageName()));
		changeImage(R.id.img_table2, getResources().getIdentifier(table[2].getImage(), "drawable", getPackageName()));
		changeImage(R.id.img_hand0, getResources().getIdentifier(hand[0].getImage(), "drawable", getPackageName()));
		changeImage(R.id.img_hand1, getResources().getIdentifier(hand[1].getImage(), "drawable", getPackageName()));
		changeImage(R.id.img_hand2, getResources().getIdentifier(hand[2].getImage(), "drawable", getPackageName()));
	}
	
	private void changeImage(int view, int img){
		ImageView image = (ImageView) findViewById(view);
		image.setImageResource(img);
	}
	
	private void switchButton(int b, boolean s){
		Button btn = (Button) findViewById(b);
		btn.setEnabled(s);
	}
	
}
