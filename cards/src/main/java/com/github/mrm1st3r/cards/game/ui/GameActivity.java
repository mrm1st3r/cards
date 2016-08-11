package com.github.mrm1st3r.cards.game.ui;

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

import com.github.mrm1st3r.cards.Constant;
import com.github.mrm1st3r.cards.R;
import com.github.mrm1st3r.cards.game.Command;
import com.github.mrm1st3r.cards.game.ThirtyOne;
import com.github.mrm1st3r.libdroid.display.BitmapUtil;
import com.github.mrm1st3r.libdroid.display.DimensionUtil;

/**
 * This is the base user interface for both, host and client.
 * 
 * @author Sergius Maier, Lukas 'mrm1st3r' Taake
 * @version 1.1.0
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
	 * Size for rival card stacks.
	 */
	private static final int RIVAL_CARD_SIZE = 50;
	/**
	 * Debug tag.
	 */
	private static final String TAG = GameActivity.class.getSimpleName();

	/**
	 * The card images that are currently shown as hand cards.
	 */
	private String[] mHandCards = new String[ThirtyOne.HAND_SIZE];
	/**
	 * The card images that are currently shown as table cards.
	 */
	private String[] mTableCards = new String[ThirtyOne.HAND_SIZE];
	/**
	 * All current players.
	 */
	private Collection<String> mPlayerList;
	/**
	 * The backside for all cards.
	 */
	private Bitmap mBg;
	/**
	 * Selected table card to swap.
	 */
	private int mCheckedTable = -1;
	/**
	 * Selected hand card to swap.
	 */
	private int mCheckedHand = -1;
	/**
	 * Local player name.
	 */
	private String mLocalName;

	/**
	 * All currently playing opponent players.
	 */
	private LinkedList<TextView> mOpponents = new LinkedList<TextView>();
	/**
	 * All table card views.
	 */
	private LinkedList<ImageView> mImgTable = new LinkedList<ImageView>();
	/**
	 * All hand card views.
	 */
	private LinkedList<ImageView> mImgHand = new LinkedList<ImageView>();
	/**
	 * All action buttons.
	 */
	private LinkedList<Button> mBtnAction = new LinkedList<Button>();
	/**
	 * List of all opponent players.
	 */
	private LinearLayout mLstPlayers;
	/**
	 * Shown player status.
	 */
	private TextView mLblStatus;
	/**
	 * Shown game message.
	 */
	private TextView mLblMessage;

	@Override
	protected void onCreate(final Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_game);
		Log.d(TAG, "Creating card lists");

		mLocalName = getIntent().getExtras().getString(
				Constant.EXTRA_LOCAL_NAME);

		mImgTable.add((ImageView) findViewById(R.id.img_table0));
		mImgTable.add((ImageView) findViewById(R.id.img_table1));
		mImgTable.add((ImageView) findViewById(R.id.img_table2));

		mImgHand.add((ImageView) findViewById(R.id.img_hand0));
		mImgHand.add((ImageView) findViewById(R.id.img_hand1));
		mImgHand.add((ImageView) findViewById(R.id.img_hand2));

		mBtnAction.add((Button) findViewById(R.id.btn_1card));
		mBtnAction.add((Button) findViewById(R.id.btn_allcards));
		mBtnAction.add((Button) findViewById(R.id.btn_push));
		mBtnAction.add((Button) findViewById(R.id.btn_knock));

		mLblStatus = (TextView) findViewById(R.id.lbl_stats);
		mLblMessage = (TextView) findViewById(R.id.lbl_message);

		mLstPlayers = (LinearLayout) findViewById(R.id.lay_rivals);

		mBg = BitmapUtil.decodeSampledBitmapFromResource(getResources(),
				BitmapUtil.getDrawableIdentifier(this, "card_background_2"),
				mImgTable.get(0).getWidth(), mImgTable.get(0).getHeight());
	}

	/**
	 * Set a player list.
	 * @param pPlayers List of player names
	 */
	protected void setPlayerList(final Collection<String> pPlayers) {
		mPlayerList = pPlayers;
	}

	/**
	 * Process a message that is sent from the game to a player.
	 * 
	 * @param msg
	 *            Message that was sent
	 */
	public final void handleMessage(final String msg) {
		Log.d(TAG, "Check incoming message: " + msg);
		if (msg.length() == 0) {
			Log.w(TAG, "empty message incoming");
			return;
		}

		Command comm = new Command(msg);

		if (comm.equals("active")) {
			enableInput();
		} else if (comm.equals("takechoice")) {
			takeChoice();
			
		} else if (comm.equals("hand")) {
			mHandCards = comm.getArgs();
			updateCardList(mImgHand, mHandCards);
			
		} else if (comm.equals("table")) {
			mTableCards = comm.getArgs();
			updateCardList(mImgTable, mTableCards);
			
		} else if (comm.equals("msg")) {
			mLblMessage.setText(comm.getArg(0));
			
		} else if (comm.equals("status")) {
			mLblStatus.setText(comm.getArg(0));
			
		} else if (comm.equals("left")) {
			removeOpponent(comm.getArg(0));
			
		} else {
			Log.w(TAG, "unknown command: " + comm.getCommand());
		}
	}

	/**
	 * Activates all buttons for an active player.
	 */
	private void enableInput() {
		for (Button b : mBtnAction) {
			b.setEnabled(true);
		}
	}

	/**
	 * Deactivates all buttons for a player who is waiting.
	 */
	private void disableInput() {
		for (Button b : mBtnAction) {
			b.setEnabled(false);
		}
	}

	/**
	 * Gives the dealer the choice between the hand cards and the hidden cards
	 * at the beginning of a round.
	 */
	private void takeChoice() {
		alertBox("Ihre Wahl",
				"Wollen Sie die Karten auf der Hand behalten oder "
						+ "mit den verdeckten Karten auf dem Tisch spielen",
						"Hand", "Tisch", "choice hand", "choice table");
	}

	/**
	 * Updates a list of cards.
	 * 
	 * @param images
	 *            List of images to update
	 * @param cards
	 *            Names of new cards to be shown
	 */
	private void updateCardList(final List<ImageView> images,
			final String[] cards) {

		for (int i = 0; i < images.size(); i++) {
			Bitmap cardImg;
			if (i >= cards.length || cards[i] == null) {
				cardImg = mBg;
			} else {
				cardImg = BitmapUtil.decodeSampledBitmapFromResource(
						getResources(),
						BitmapUtil.getDrawableIdentifier(this, cards[i]),
						images.get(i).getWidth(), images.get(i).getHeight());
			}

			images.get(i).setImageBitmap(cardImg);
		}
	}

	/**
	 * Show name and image for playing players.
	 */
	protected void showOpponentList() {
		
		if (mPlayerList == null) {
			return;
		}

		for (String p : mPlayerList) {

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
			mLstPlayers.addView(t);
			mOpponents.add(t);
		}
	}

	/**
	 * Remove a player from opponent list.
	 * 
	 * @param name
	 *            Opponent name
	 */
	private void removeOpponent(final String name) {
		for (TextView t : mOpponents) {
			if (t.getText().equals(name)) {
				mOpponents.remove(t);
				return;
			}
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
	 * Save the marked card on the table.
	 * 
	 * @param view
	 *            ID of the ImageView which is marked
	 */
	public final void onTableClick(final View view) {
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

	/**
	 * Reset all table cards to 100% opacity.
	 */
	private void resetTableCards() {
		for (ImageView v : mImgTable) {
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
	public final void onHandClick(final View view) {
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

	/**
	 * Reset all hand cards to 100% opacity.
	 */
	private void resetHandCards() {
		for (ImageView v : mImgHand) {
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
	public final void onActionClick(final View view) {
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

	/**
	 * Send a message from the player to the game logic.
	 * 
	 * @param msg
	 *            Message to send
	 */
	public abstract void sendMessage(String msg);
}
