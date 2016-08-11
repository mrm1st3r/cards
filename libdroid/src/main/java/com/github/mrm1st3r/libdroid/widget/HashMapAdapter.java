package com.github.mrm1st3r.libdroid.widget;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * Base adapter for HashMap based lists.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0.1
 *
 * @param <K>
 *            key data type
 * @param <V>
 *            value data type
 */
public abstract class HashMapAdapter<K, V> extends BaseAdapter {

	/**
	 * Wrapped HashMap.
	 */
	private HashMap<K, V> mData = null;
	/**
	 * Current application context.
	 */
	private Context mContext;

	/**
	 * Construct a new HashMapAdapter with empty content.
	 * @param c application context
	 */
	public HashMapAdapter(final Context c) {
		mContext = c;
		mData = new HashMap<K, V>();
	}
	
	/**
	 * Create a new HashMapAdapter.
	 * 
	 * @param c
	 *            application context
	 * @param data
	 *            data structure to be used
	 */
	public HashMapAdapter(final Context c, final HashMap<K, V> data) {
		mData = data;
		mContext = c;
	}

	/**
	 * Add a new item to the hashmap.
	 * @param key Key
	 * @param value Value
	 */
	public void addItem(final K key, final V value) {
		mData.put(key, value);
	}

	@Override
	public final int getCount() {
		return mData.size();
	}

	@Override
	public final V getItem(final int position) {
		return mData.get(getKey(position));
	}

	@Override
	public final long getItemId(final int position) {
		return position;
	}

	/**
	 * @return The current set application context
	 */
	public final Context getContext() {
		return mContext;
	}

	/**
	 * Get the key for a key-value pair.
	 * 
	 * @param pos
	 *            The pairs position inside the data structure
	 * @return The key
	 */
	protected final K getKey(final int pos) {
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
