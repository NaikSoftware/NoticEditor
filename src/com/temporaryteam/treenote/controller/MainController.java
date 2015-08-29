package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.format.AutoFormat;
import com.temporaryteam.treenote.io.export.Exporter;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.PreviewStyles;
import com.temporaryteam.treenote.view.Chooser;
import com.temporaryteam.treenote.view.ImportHtmlWindow;
import com.temporaryteam.treenote.view.Notification;
import com.temporaryteam.treenote.view.SimpleAlert;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private CheckMenuItem wordWrapItem;
    @FXML
    private MenuItem itemImportFromWeb, switchOrientationItem;
    @FXML
    private Menu previewStyleMenu;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private SplitPane mainPane;
    @FXML
    private CheckMenuItem hideEditorMenuItem;
    @FXML
    private ResourceBundle resources; // magic!
    @FXML
    private NoticeTreeController noticeTreeController;
    @FXML
    private NoticeEditorController noticeEditorController;

    private File fileSaved;
    private ImportHtmlWindow importHtmlWindow = new ImportHtmlWindow();
    private BooleanProperty saved = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        progressBar.managedProperty().bind(progressBar.visibleProperty()); // for hide progress bar

        // Set preview styles menu items
        ToggleGroup previewStyleGroup = new ToggleGroup();
        for (PreviewStyles style : PreviewStyles.values()) {
            final String cssPath = style.getCssPath();
            RadioMenuItem item = new RadioMenuItem(style.getName());
            item.setToggleGroup(previewStyleGroup);
            item.setOnAction(event -> {
                String path = cssPath;
                if (path != null) {
                    path = getClass().getResource(path).toExternalForm();
                }
                noticeEditorController.setWebStylesheet(path);
            });
            if (style == PreviewStyles.DEFAULT) {
                noticeEditorController.setWebStylesheet(getClass().getResource(cssPath).toExternalForm());
                item.setSelected(true);
            }
            previewStyleMenu.getItems().add(item);
        }

        saved.addListener(observable -> updateWindowTitle()); // update only from this listener
        Context.getPrimaryStage().setOnCloseRequest(event -> {
            if (!saved.get()) {
                SimpleAlert.confirm(tr("not_saved_alert"))
                        .filter(btn -> btn != ButtonType.APPLY)
                        .ifPresent(btn -> event.consume());
            }
        });
        noticeEditorController.currentNotice().bind(noticeTreeController.currentNoticeProperty());
        noticeTreeController.currentNoticeProperty().addListener((observable, oldValue, newValue) -> {
            boolean isBranch = newValue == null || newValue.isBranch();
            itemImportFromWeb.setDisable(isBranch);
        });
        Platform.runLater(this::postInit);
    }

    /* Called when all views initialized */
    private void postInit() {
        SplitPane editorPanel = Context.findById(mainPane, "#editorPanel");
        TextArea noticeArea = Context.findById(mainPane, "#noticeArea");
        TreeView noticeTreeView = Context.findById(mainPane, "#noticeTreeView");

        hideEditorMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            editorPanel.getDividers().get(0).setPosition(newValue ? 0 : 0.5);
        });
        switchOrientationItem.setOnAction(event -> {
            editorPanel.setOrientation(editorPanel.getOrientation() == Orientation.HORIZONTAL
                    ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        });
        noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());
        noticeTreeView.rootProperty().addListener(observable -> {
            if (!saved.get()) updateWindowTitle(); // force update when root changed if already "not saved"
            else saved.set(false); // or regular update title
            noticeTreeView.getRoot().addEventHandler(NoticeTree.documentChangedEvent(), event -> saved.set(false));
        });
        noticeTreeController.rebuildTree(tr("help_msg"));
    }

    @FXML
    private void handleNew(ActionEvent event) {
        SimpleAlert.confirm(tr("not_saved_alert"))
                .filter(btn -> btn == ButtonType.APPLY)
                .ifPresent(btn -> {
                    fileSaved = null;
                    noticeTreeController.rebuildTree(tr("help_msg"));
                });
    }

    @FXML
    private void handleOpen(ActionEvent event) {
        try {
            fileSaved = Chooser.file().open()
                    .filter(Chooser.SUPPORTED, Chooser.ALL)
                    .title("Open notice")
                    .show();
            if (fileSaved == null) {
                return;
            }
            noticeTreeController.openTree(AutoFormat.open(fileSaved));
            saved.set(true); // open from FS - already saved
        } catch (IOException | JSONException e) {
            SimpleAlert.error(tr("open_error"), e);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (fileSaved == null) {
            handleSaveAs(event);
        } else {
            saveDocument(fileSaved);
        }
    }

    @FXML
    private void handleSaveAs(ActionEvent event) {
        fileSaved = Chooser.file().save()
                .filter(Chooser.ZIP, Chooser.JSON)
                .title("Save notice")
                .show();
        if (fileSaved == null)
            return;

        saveDocument(fileSaved);
    }

    private void saveDocument(File file) {
        if (Chooser.JSON.equals(Chooser.getLastSelectedExtensionFilter())
                || file.getName().toLowerCase().endsWith(".json")) {
            processExport(Exporter.JSON.setup(file, noticeTreeController.getNoticeTree()));
        } else {
            processExport(Exporter.ZIP.setup(file, noticeTreeController.getNoticeTree()));
        }
    }

    @FXML
    private void handleExportHtml(ActionEvent event) {
        File destDir = Chooser.directory()
                .title("Select directory to save HTML files")
                .show();
        if (destDir == null) return;
        processExport(Exporter.HTML.setup(destDir, noticeTreeController.getNoticeTree()));
    }

    @FXML
    private void handleImportFromWeb(ActionEvent event) {
        importHtmlWindow.showAndWait((html, error) -> {
            importHtmlWindow.close();
            if (html == null) {
                SimpleAlert.error(tr("loading_error"), error);
            } else {
                noticeEditorController.setText(html);
            }
        });
    }

    private void processExport(Exporter exporter) {
        toggleWaiting(true);
        exporter.export(error -> {
            toggleWaiting(false);
            if (error == null) {
                saved.set(true);
                Notification.success(tr("saved"));
            } else {
                SimpleAlert.error(tr("save_error"), error);
            }
            return null;
        });
    }

    @FXML
    private void handleExit(ActionEvent event) {
        SimpleAlert.confirm(tr("not_saved_alert"))
                .filter(btn -> btn == ButtonType.APPLY)
                .ifPresent(btn -> Platform.exit());
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        Context.loadToNewStage("InfoFrame").showAndWait();
    }

    private void toggleWaiting(boolean wait) {
        progressBar.setVisible(wait);
        mainPane.setDisable(wait);
        menuBar.setDisable(wait);
    }

    private void updateWindowTitle() {
        String filepath = fileSaved == null ? resources.getString("undefined") : fileSaved.getAbsolutePath();
        String title = "TreeNote - " + filepath;
        if (!saved.get()) title += resources.getString("not_saved");
        Context.getPrimaryStage().setTitle(title);
    }

    /**
     * Translate string by key
     *
     * @param key
     * @return translated string
     */
    private String tr(String key) {
        return resources.getString(key);
    }

}
