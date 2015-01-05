package com.github.mrm1st3r.btutil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.SparseArray;

/**
 * Utility class for managing Bluetooth connections.
 * @author Lukas 'mrm1st3r' Taake
 */
public final class BtUtil {

	/**
	 * Debug tag.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = BtUtil.class.getSimpleName();
	/**
	 * Request code for enabling bluetooth.
	 */
	private static final int REQUEST_ENABLE_BT = 1;
	/**
	 * Request code for enabling bluetooth discoverability.
	 */
	private static final int REQUEST_ENABLE_DISCOVERABLE = 2;
	/**
	 * Registered callbacks for bluetooth requests.
	 */
	private static SparseArray<ResultAction> callbacks = 
			new SparseArray<ResultAction>();
	/**
	 * Bluetooth adapter to use.
	 */
	private static BluetoothAdapter adapter =
			BluetoothAdapter.getDefaultAdapter();

	/**
	 * Hidden constructor for utility class.
	 */
	private BtUtil() { }

	/**
	 * @return Bluetooth support
	 */
	public static boolean isSupported() {
		return adapter != null;
	}

	/**
	 * @return Bluetooth status
	 */
	public static boolean isEnabled() {
		return adapter.isEnabled();
	}

	/**
	 * Enable bluetooth.
	 * @param context current activity context.
	 * @param act callback to be executed afterwards.
	 */
	public static void enable(final Activity context, final ResultAction act) {
		Intent enableInt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		context.startActivityForResult(enableInt, REQUEST_ENABLE_BT);

		callbacks.put(REQUEST_ENABLE_BT, act);
	}

	/**
	 * Enable Bluetooth discoverability.
	 * @param context application context
	 * @param time number of seconds to activate discoverability
	 * @param act callback to be executed afterwards.
	 */
	public static void enableDiscoverability(final Activity context,
			final int time, final ResultAction act) {

		Intent discoverableIntent =
				new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
		
		context.startActivityForResult(discoverableIntent,
				REQUEST_ENABLE_DISCOVERABLE);

		callbacks.put(REQUEST_ENABLE_DISCOVERABLE, act);
	}

	/**
	 * Receiver for fetching callbacks.
	 * Should be called in Activities onActivityResult in the beginning.
	 * @param context current activity context
	 * @param reqCode the results request code
	 * @param resultCode the result code
	 * @param data additional received data
	 * @return true if the received activity result was handled by this class.
	 */
	public static boolean onActivityResult(final Activity context,
			final int reqCode, final int resultCode, final Intent data) {
		
		ResultAction act = callbacks.get(reqCode);

		// any other intent, not created by BtUtil.
		if (act == null) {
			return false;
		}
		callbacks.remove(reqCode);
		
		// Log.d(TAG, "Received result " + resultCode + " for code " + reqCode);
		
		if (resultCode != Activity.RESULT_CANCELED) {
			act.onSuccess();
		} else {
			act.onFailure();
		}
		
		return true;
	}

	public static void findDevices() {
		
	}

	public static String getDeviceName() {
		return adapter.getName();
	}
}