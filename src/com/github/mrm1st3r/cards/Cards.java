package com.github.mrm1st3r.cards;

import java.util.HashMap;

import android.app.Application;

import com.github.mrm1st3r.connection.bluetooth.SimpleBluetoothConnection;

public class Cards extends Application {

	public HashMap<SimpleBluetoothConnection, String> connections =
			new HashMap<SimpleBluetoothConnection, String>();
}
