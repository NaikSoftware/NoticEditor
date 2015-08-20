package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.io.importers.WebImporter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Settings for import from Web.
 *
 * @author Naik
 */
public class ImportHtmlController implements Initializable {

    @FXML
    private ChoiceBox<WebImporter.Mode> choiceBoxModes;

    @Override
    public void initialize(URL location, ResourceBundle res) {
        choiceBoxModes.setItems(FXCollections.observableArrayList(WebImporter.Mode.values()));
        choiceBoxModes.getSelectionModel().selectFirst();
    }

    public WebImporter.Mode getSelectedMode() {
        return choiceBoxModes.getValue();
    }

}
