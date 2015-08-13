package com.temporaryteam.treenote.io;

import com.temporaryteam.treenote.model.NoticeTree;
import java.io.File;
import javafx.application.Platform;
import javafx.util.Callback;

/**
 * Export notices.strategy.
 * @author aNNiMON
 */
public abstract class ExportStrategy {
	
	public abstract void export(File file, NoticeTree tree) throws ExportException;
	
	public void exportAsync(File file, NoticeTree tree, Callback<Exception, Void> callback) {
		new Thread(() -> {
			try {
				export(file, tree);
				Platform.runLater(() -> callback.call(null));
			} catch (ExportException ex) {
				Platform.runLater(() -> callback.call(ex));
			}
		}).start();
	}
	
}
