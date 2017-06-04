package com.github.mrm1st3r.cards.activity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mrm1st3r.cards.Cards;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.Command;
import com.github.mrm1st3r.libdroid.display.BitmapUtil;
import com.github.mrm1st3r.libdroid.display.DimensionUtil;

/**
 * This is the base user interface for both, host and client.
 */
public abstract class GameActivity extends Activity {

	private static final int DEFAULT_OPACITY = 255;
	private static final int SELECTED_OPACITY = 150;
	private static final int RIVAL_CARD_SIZE = 50;
	private static final String TAG = GameActivity.class.getSimpleName();

	private Collection<String> currentPlayers;
	private Bitmap backImage;
	private int mCheckedTable = -1;
	private int mCheckedHand = -1;
	private String mLocalName;

	private LinkedList<TextView> opponentPlayers = new LinkedList<>();
	private LinearLayout opponentPlayerList;
	private LinkedList<ImageView> tableCardImages = new LinkedList<>();
	private LinkedList<ImageView> handCardImages = new LinkedList<>();
	private LinkedList<Button> actionButtons = new LinkedList<>();
	private TextView playerStatusLabel;
	private TextView gameMessageLabel;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_game);
		Log.d(TAG, "Creating card lists");
		mLocalName = getIntent().getExtras().getString(Cards.EXTRA_LOCAL_NAME);

		tableCardImages.add((ImageView) findViewById(R.id.img_table0));
		tableCardImages.add((ImageView) findViewById(R.id.img_table1));
		tableCardImages.add((ImageView) findViewById(R.id.img_table2));

		handCardImages.add((ImageView) findViewById(R.id.img_hand0));
		handCardImages.add((ImageView) findViewById(R.id.img_hand1));
		handCardImages.add((ImageView) findViewById(R.id.img_hand2));

		actionButtons.add((Button) findViewById(R.id.btn_1card));
		actionButtons.add((Button) findViewById(R.id.btn_allcards));
		actionButtons.add((Button) findViewById(R.id.btn_push));
		actionButtons.add((Button) findViewById(R.id.btn_knock));

		playerStatusLabel = (TextView) findViewById(R.id.lbl_stats);
		gameMessageLabel = (TextView) findViewById(R.id.lbl_message);

		opponentPlayerList = (LinearLayout) findViewById(R.id.lay_rivals);

		backImage = BitmapUtil.decodeSampledBitmapFromResource(getResources(),
				BitmapUtil.getDrawableIdentifier(this, "card_background_2"),
				tableCardImages.get(0).getWidth(), tableCardImages.get(0).getHeight());
	}

	void setPlayerList(Collection<String> pPlayers) {
		currentPlayers = pPlayers;
	}

	public void handleMessage(String msg) {
		Log.d(TAG, "Check incoming message: " + msg);
		if (msg.length() == 0) {
			Log.w(TAG, "empty message incoming");
			return;
		}
		Command command = new Command(msg);
		String comm = command.getCommand();
		switch (comm) {
			case "active":
				enableInput();
				break;
			case "takechoice":
				takeChoice();
				break;
			case "hand":
				String[] handCards = command.getArgs();
				updateCardList(handCardImages, handCards);
				break;
			case "table":
				String[] tableCards = command.getArgs();
				updateCardList(tableCardImages, tableCards);
				break;
			case "msg":
				gameMessageLabel.setText(command.getArg(0));
				break;
			case "status":
				playerStatusLabel.setText(command.getArg(0));
				break;
			case "left":
				removeOpponent(command.getArg(0));
				break;
			default:
				Log.w(TAG, "unknown command: " + command.getCommand());
				break;
		}
	}

	private void enableInput() {
		for (Button b : actionButtons) {
			b.setEnabled(true);
		}
	}

	private void disableInput() {
		for (Button b : actionButtons) {
			b.setEnabled(false);
		}
	}

	private void takeChoice() {
		alertBox("Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder "
						+ "mit den verdeckten Karten auf dem Tisch spielen",
						"Hand", "Tisch", "choice hand", "choice table");
	}

	private void updateCardList(List<ImageView> images, String[] cards) {
		for (int i = 0; i < images.size(); i++) {
			Bitmap cardImg;
			if (i >= cards.length || cards[i] == null) {
				cardImg = backImage;
			} else {
				cardImg = BitmapUtil.decodeSampledBitmapFromResource(
						getResources(),
						BitmapUtil.getDrawableIdentifier(this, cards[i]),
						images.get(i).getWidth(), images.get(i).getHeight());
			}
			images.get(i).setImageBitmap(cardImg);
		}
	}

	protected void showOpponentList() {
		if (currentPlayers == null) {
			return;
		}
		for (String p : currentPlayers) {
			if (p.equals(mLocalName)) {
				continue;
			}
			TextView t = new TextView(this);
			t.setText(p);
			t.setCompoundDrawables(
					null,
					new BitmapDrawable(getResources(), BitmapUtil
							.decodeSampledBitmapFromResource(getResources(),
									R.drawable.card_rival,
									DimensionUtil.dpToPx(RIVAL_CARD_SIZE),
									DimensionUtil.dpToPx(RIVAL_CARD_SIZE))),
									null, null);
			opponentPlayerList.addView(t);
			opponentPlayers.add(t);
		}
	}

	private void removeOpponent(String name) {
		for (TextView t : opponentPlayers) {
			if (t.getText().equals(name)) {
				opponentPlayers.remove(t);
				return;
			}
		}
	}

	private void alertBox(String title, String msg, String yes, String no,
						  final String yesMsg, final String noMsg) {
		final Context context = this;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
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
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void onTableClick(View view) {
		resetTableCards();
		((ImageView) view).setAlpha(SELECTED_OPACITY);
		switch (view.getId()) {
		case R.id.img_table0:
			mCheckedTable = 0;
			break;
		case R.id.img_table1:
			mCheckedTable = 1;
			break;
		case R.id.img_table2:
			mCheckedTable = 2;
			break;
		default:
			mCheckedTable = -1;
		}
	}

	private void resetTableCards() {
		for (ImageView v : tableCardImages) {
			if (v == null) {
				Log.d(TAG, "table card fail");
				continue;
			}
			v.setAlpha(DEFAULT_OPACITY);
		}
	}

	public void onHandClick(View view) {
		resetHandCards();
		((ImageView) view).setAlpha(SELECTED_OPACITY);
		switch (view.getId()) {
		case R.id.img_hand0:
			mCheckedHand = 0;
			break;
		case R.id.img_hand1:
			mCheckedHand = 1;
			break;
		case R.id.img_hand2:
			mCheckedHand = 2;
			break;
		default:
			mCheckedHand = -1;
		}
	}

	private void resetHandCards() {
		for (ImageView v : handCardImages) {
			if (v == null) {
				Log.d(TAG, "hand card fail");
				continue;
			}
			v.setAlpha(DEFAULT_OPACITY);
		}
	}

	public void onActionClick(View view) {
		switch (view.getId()) {

		case R.id.btn_1card:
			if (mCheckedTable == -1 && mCheckedHand == -1) {
				return;
			}
			sendMessage("swap " + mCheckedHand + " " + mCheckedTable);
			mCheckedTable = -1;
			mCheckedHand = -1;
			resetHandCards();
			resetTableCards();
			break;

		case R.id.btn_allcards:
			sendMessage("swapall");

		case R.id.btn_knock:
			sendMessage("close");

		case R.id.btn_push:
			sendMessage("push");

		default:
			break;
		}
		disableInput();
	}

	public abstract void sendMessage(String msg);
}
