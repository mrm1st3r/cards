package com.github.mrm1st3r.libdroid.util;

/**
 * Basic callback interface with different success and failure actions.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.1
 */
public abstract class ResultAction {

	/**
	 * Action to be executed on success.
	 */
	public void onSuccess() {

	}

	/**
	 * Action to be executed on failure.
	 */
	public void onFailure() {

	}
}
