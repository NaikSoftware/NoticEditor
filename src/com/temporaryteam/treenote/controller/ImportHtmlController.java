package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.io.importers.HtmlImportMode;
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
    private ChoiceBox<HtmlImportMode> choiceBoxModes;

    @Override
    public void initialize(URL location, ResourceBundle res) {
        choiceBoxModes.setConverter(new StringConverter<HtmlImportMode>() {

            @Override
            public String toString(HtmlImportMode mode) {
				return res.getString(mode.getName());
            }

            @Override
            public HtmlImportMode fromString(String string) {
                return null;
            }
        });

        choiceBoxModes.setItems(FXCollections.observableArrayList(HtmlImportMode.values()));
        choiceBoxModes.getSelectionModel().selectFirst();
    }

    public HtmlImportMode getSelectedMode() {
        return choiceBoxModes.getValue();
    }

}
