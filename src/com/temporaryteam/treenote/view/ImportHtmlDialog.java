package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.controller.ImportHtmlController;
import com.temporaryteam.treenote.importer.WebImporter;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author Naik
 */
public class ImportHtmlDialog extends TextInputDialog {

    private ImportHtmlController controller;

    public ImportHtmlDialog(String header, ResourceBundle res) {
        super("http://");
        initOwner(Context.getPrimaryStage());
        setHeaderText(header);

        try {
            FXMLLoader loader = new FXMLLoader(Context.getFXML("ImportHtmlDialog"), res);
            getDialogPane().setExpandableContent(loader.load());
            getDialogPane().setExpanded(true);
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load fxml for ImportHtmlDialog");
        }
    }

    public WebImporter.Mode getSelectedMode() {
        return controller.getSelectedMode();
    }

}
