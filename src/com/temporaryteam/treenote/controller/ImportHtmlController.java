package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.io.importers.ImportCallback;
import com.temporaryteam.treenote.io.importers.WebImporter;
import com.temporaryteam.treenote.view.SimpleAlert;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Settings for import from Web.
 *
 * @author aNNiMON, Naik
 */
public class ImportHtmlController implements Initializable {

    @FXML
    private VBox modesBox;
    @FXML
    private WebView pagePreview;
    @FXML
    private TextField urlField;

    private static final WebImporter.Mode DEFAULT_MODE = WebImporter.Mode.ORIGINAL;
    private WebImporter importer = new WebImporter(DEFAULT_MODE);
    private ImportCallback importCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Node> nodes = modesBox.getChildren();
        nodes.clear();
        final ToggleGroup modesGroup = new ToggleGroup();
        for (WebImporter.Mode mode : WebImporter.Mode.values()) {
            RadioButton radio = new RadioButton(mode.toString());
            radio.setOnAction(e -> onModeChanged(mode));
            radio.setToggleGroup(modesGroup);
            nodes.add(radio);
            if (mode == DEFAULT_MODE) radio.setSelected(true);
        }

        pagePreview.getEngine().loadContent(resources.getString("preview"), "text/html");
    }

    public void setImportCallback(ImportCallback importCallback) {
        this.importCallback = importCallback;
    }

    private void onModeChanged(WebImporter.Mode m) {
        importer.newMode(m);
        pagePreview.getEngine().loadContent("");
    }

    @FXML
    private void handlePreview(ActionEvent event) {
        importer.grab(urlField.getText(), (html, error) -> {
            if (html != null) {
                pagePreview.getEngine().loadContent(html);
            } else {
                SimpleAlert.error(Context.getResources().getString("loading_error"), error);
            }
        });
    }

    @FXML
    private void handleImport(ActionEvent event) {
        importer.grab(urlField.getText(), importCallback);
    }
}
