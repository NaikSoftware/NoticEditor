package com.temporaryteam.treenote.io;

/**
 * Listener used in async saving.
 *
 * @author Naik
 */
public interface SaveListener {

	void onComplete();

	void onError(ExportException ex);
}
