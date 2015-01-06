package com.github.mrm1st3r.util;

/**
 * Basic callback interface with different success and failure actions.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.1.0
 *
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
