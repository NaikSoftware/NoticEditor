package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.importer.WebImporter;
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
        choiceBoxModes.setConverter(new StringConverter<WebImporter.Mode>() {

            @Override
            public String toString(WebImporter.Mode mode) {
                switch (mode) {
                    case BASIC_WITH_IMAGES:
                        return res.getString("basic_with_img");
                    case ONLY_TEXT:
                        return res.getString("only_text");
                    case RELAXED:
                        return res.getString("relaxed");
                    case SIMPLE_TEXT:
                        return res.getString("simple_text");
                    case ORIGINAL:
                        return res.getString("original");
                    default:
                        BASIC:
                        return res.getString("basic");
                }
            }

            @Override
            public WebImporter.Mode fromString(String string) {
                return null;
            }
        });

        choiceBoxModes.setItems(FXCollections.observableArrayList(WebImporter.Mode.values()));
        choiceBoxModes.getSelectionModel().selectFirst();
    }

    public WebImporter.Mode getSelectedMode() {
        return choiceBoxModes.getValue();
    }

}
