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
 * Container for all user interface elements that are visible to both, host and clients.
 */
public class LobbyFragment extends Fragment {

	private ListView playerList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_lobby, container, false);
		playerList = (ListView) v;
		TextView header = new TextView(getActivity());
		header.setText(getActivity().getString(R.string.connected_players));
		playerList.addHeaderView(header);
		return v;
	}

	public void setAdapter(BaseAdapter adapter) {
		playerList.setAdapter(adapter);
	}
}
