package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.model.NoticeTree;
import javafx.application.Platform;
import javafx.util.Callback;

/**
 * @author Naik
 */
public abstract class Exporter {

    private NoticeTree tree;

    Exporter(NoticeTree tree) {
        this.tree = tree;
    }

    NoticeTree getTree() {
        return tree;
    }

    abstract void export() throws ExportException;

    public void export(Callback<Exception, Void> callback) {
        new Thread(() -> {
            try {
                export();
                Platform.runLater(() -> callback.call(null));
            } catch (ExportException ex) {
                Platform.runLater(() -> callback.call(ex));
            }
        }).start();
    }
}
