package com.github.mrm1st3r.btutil;

/**
 * Basic callback interface with different success and failure actions.
 * 
 * @author Lukas 'mrm1st3r' Taake
 * @version 1.0
 *
 */
public interface ResultAction {

	/**
	 * Action to be executed on success.
	 */
	void onSuccess();

	/**
	 * Action to be executed on failure.
	 */
	void onFailure();
}
