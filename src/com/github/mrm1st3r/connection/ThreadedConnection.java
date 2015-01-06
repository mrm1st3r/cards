package com.github.mrm1st3r.connection;

import java.io.Closeable;

public abstract class ThreadedConnection extends Thread implements Closeable {

	protected abstract void onRun();

	public abstract void setOnConnectionChangeHandler(OnConnectionChangeHandler h);
}
