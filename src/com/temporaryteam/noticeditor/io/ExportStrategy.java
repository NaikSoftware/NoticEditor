package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;
import javafx.application.Platform;

/**
 * Export notices.strategy.
 * @author aNNiMON
 */
public abstract class ExportStrategy {
	
	public abstract void export(File file, NoticeTree tree) throws ExportException;
	
	public void exportAsync(File file, NoticeTree tree, SaveListener listener) {
		new Thread(() -> {
			try {
				export(file, tree);
				Platform.runLater(() -> listener.onComplete());
			} catch (ExportException ex) {
				Platform.runLater(() -> listener.onError(ex));
			}
		}).start();
	}
	
}
