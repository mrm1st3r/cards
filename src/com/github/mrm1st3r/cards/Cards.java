package com.github.mrm1st3r.cards;

import java.util.HashMap;

import android.app.Application;

import com.github.mrm1st3r.btutil.BluetoothConnection;

public class Cards extends Application {

	public HashMap<BluetoothConnection, String> connections = new HashMap<BluetoothConnection, String>();
}
