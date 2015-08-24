package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.io.export.Exporter;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.pegdown.PegDownProcessor;

import java.net.URL;
import java.util.ResourceBundle;

import static org.pegdown.Extensions.*;

/**
 * @author Naik
 */
public class NoticeEditorController implements Initializable {

    @FXML
    private SplitPane editorPanel;
    @FXML
    private TextArea noticeArea;
    @FXML
    private WebView viewer;

    private WebEngine engine;
    private PegDownProcessor processor;
    private ObjectProperty<NoticeTreeItem> currentNotice = new SimpleObjectProperty<>();
    private NoticeTreeItem currentTreeitem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = viewer.getEngine();
        processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
        Exporter.HTML.setProcessor(processor);

        currentNotice.addListener((observable1, oldValue, newValue) -> changeCurrentNotice(newValue));
        noticeArea.textProperty().addListener((observable, oldVal, newVal) -> {
            engine.loadContent(processor.markdownToHtml(newVal));
            if (currentTreeitem != null) {
                currentTreeitem.changeContent(newVal);
            }
        });
    }

    public ObjectProperty<NoticeTreeItem> currentNotice() {
        return currentNotice;
    }

    public void setWebStylesheet(String path) {
        engine.setUserStyleSheetLocation(path);
    }

    public void setText(String text) {
        noticeArea.setText(text);
    }

    private void changeCurrentNotice(NoticeTreeItem item) {
        boolean isLeaf = item != null && item.isLeaf();
        currentTreeitem = isLeaf ? item : null;
        noticeArea.setText(isLeaf ? item.getContent() : "");
        noticeArea.setDisable(!isLeaf);
    }
}
