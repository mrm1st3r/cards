package com.github.mrm1st3r.util;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class HashMapAdapter<K, V> extends BaseAdapter {
	
	private HashMap<K, V> mData = null;
	protected Context context;

	public HashMapAdapter(Context c, HashMap<K, V> data) {
		mData  = data;
		context = c;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public V getItem(int position) {
		return mData.get(getKey(position));
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	protected K getKey(int pos) {
		Iterator<K> it = mData.keySet().iterator();
		K key = null;
		for (int i = 0; i <= pos; i++) {
			if (it.hasNext()) {
				key = it.next();
			} else {
				return null;
			}
		}
		return key;
	}
}
