package com.github.mrm1st3r.cards.lobby;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mrm1st3r.cards.R;

/**
 * Container for all user interface elements that are visible to both, host and
 * clients.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 */
public class LobbyFragment extends Fragment {

	/**
	 * List displaying all players.
	 */
	private ListView list;

	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_lobby, container, false);
		list = (ListView) v;

		TextView header = new TextView(getActivity());
		header.setText(getActivity().getString(R.string.connected_players));
		list.addHeaderView(header);
		return v;
	}

	/**
	 * Register an adapter for the player list.
	 * 
	 * @param adapter
	 *            Player list wrapper adapter
	 */
	public final void setAdapter(final BaseAdapter adapter) {
		list.setAdapter(adapter);
	}
}
