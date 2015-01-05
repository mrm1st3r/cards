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

public class LobbyFragment extends Fragment {

	private ListView list;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_lobby, container, false);
		list = (ListView) v;
		
		TextView header = new TextView(getActivity());
		header.setText(getActivity().getResources().getString(R.string.connected_players));
		list.addHeaderView(header);
		return v;
	}

	public void setAdapter(BaseAdapter adapter) {
		list.setAdapter(adapter);
	}
}
