package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.controller.ImportHtmlController;
import com.temporaryteam.treenote.io.importers.ImportCallback;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Naik
 */
public class ImportHtmlWindow {

    private ImportHtmlController controller;
    private Stage stage;

    public ImportHtmlWindow() {
        try {
            FXMLLoader loader = Context.makeLoader("ImportHtmlWindow");
            Scene scene = new Scene(loader.load());
            stage = new Stage();
            stage.setScene(scene);
            stage.initOwner(Context.getPrimaryStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(Context.getResources().getString("import_from") + " Web");
            stage.getIcons().add(Context.ICON);
            controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Could not load fxml for ImportHtmlWindow", e);
        }
    }

    public void showAndWait(ImportCallback callback) {
        controller.setImportCallback(callback);
        stage.showAndWait();
    }

    public void close() {
        stage.close();
    }

}
