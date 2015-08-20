package com.temporaryteam.treenote.io.importers;

/**
 * @author Naik
 */
public interface ImportCallback {

    void call(String importedText, Exception ex);
}
