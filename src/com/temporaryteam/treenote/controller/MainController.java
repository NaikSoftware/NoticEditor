package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.format.AutoFormat;
import com.temporaryteam.treenote.io.export.Exporter;
import com.temporaryteam.treenote.model.NoticeStatus;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.model.PreviewStyles;
import com.temporaryteam.treenote.view.Chooser;
import com.temporaryteam.treenote.view.EditableTreeCell;
import com.temporaryteam.treenote.view.ImportHtmlWindow;
import com.temporaryteam.treenote.view.SimpleAlert;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONException;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import static org.pegdown.Extensions.*;

public class MainController {

    @FXML
    private NoticeSettingsController noticeSettingsController;
    @FXML
    private SplitPane editorPanel;
    @FXML
    private TextArea noticeArea;
    @FXML
    private WebView viewer;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem addBranchItem, addNoticeItem, deleteItem;
    @FXML
    private CheckMenuItem wordWrapItem;
    @FXML
    private Menu previewStyleMenu;
    @FXML
    private TreeView<String> noticeTreeView;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private SplitPane mainPane;
    @FXML
    private CheckMenuItem hideEditorMenuItem;
    @FXML
    private ResourceBundle resources; // magic!

    private WebEngine engine;
    private PegDownProcessor processor;
    private NoticeTree noticeTree;
    private NoticeTreeItem currentTreeItem;
    private File fileSaved;
    private BooleanProperty saved = new SimpleBooleanProperty(false);
    private boolean editing = true;
    private ImportHtmlWindow importHtmlWindow;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
        Exporter.HTML.setProcessor(processor);
        noticeSettingsController.setMainController(this);
        engine = viewer.getEngine();
        progressBar.managedProperty().bind(progressBar.visibleProperty()); // for hide progress bar
        importHtmlWindow = new ImportHtmlWindow();

        // Set preview styles menu items
        ToggleGroup previewStyleGroup = new ToggleGroup();
        for (PreviewStyles style : PreviewStyles.values()) {
            final String cssPath = style.getCssPath();
            RadioMenuItem item = new RadioMenuItem(style.getName());
            item.setToggleGroup(previewStyleGroup);
            if (cssPath == null) {
                item.setSelected(true);
            }
            item.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    String path = cssPath;
                    if (path != null) {
                        path = getClass().getResource(path).toExternalForm();
                    }
                    engine.setUserStyleSheetLocation(path);
                }
            });
            previewStyleMenu.getItems().add(item);
        }

        // Setup notice tree
        noticeTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noticeTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            setCurrentItem((NoticeTreeItem) newVal);
        });
        noticeTreeView.setCellFactory(treeView -> new EditableTreeCell());

        // Setup notice area
        noticeArea.textProperty().addListener((observable, oldVal, newVal) -> {
            engine.loadContent(processor.markdownToHtml(newVal));
            if (currentTreeItem != null && editing) {
                currentTreeItem.changeContent(newVal);
                saved.set(false); // change status only if modified content belongs to this item
            }
        });
        noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());

        // Miscellaneous listeners
        hideEditorMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            editorPanel.getDividers().get(0).setPosition(newValue ? 0 : 0.5);
        });
        Context.getPrimaryStage().setOnCloseRequest(event -> {
            if (!saved.get()) {
                SimpleAlert.confirm(tr("exit_alert")).ifPresent(btn -> {
                    if (btn != ButtonType.APPLY) event.consume();
                });
            }
        });

        // Setup saving listeners
        noticeTreeView.rootProperty().addListener(observable -> {
            if (!saved.get()) updateWindowTitle(); // force update when root changed if already "not saved"
            else saved.set(false); // or regular update title
            noticeTreeView.getRoot().addEventHandler(NoticeTree.documentChangedEvent(), event -> saved.set(false));
        });
        saved.addListener(observable -> updateWindowTitle()); // update only from this listener
        rebuildTree(tr("help_msg"));
    }

    private void rebuildTree(String defaultNoticeContent) {
        noticeTree = new NoticeTree(new NoticeTreeItem("Root"));
        NoticeTreeItem item = new NoticeTreeItem(tr("notice_name"), defaultNoticeContent, NoticeStatus.NORMAL);
        noticeTree.addItem(item, noticeTree.getRoot());
        noticeTreeView.setRoot(noticeTree.getRoot());
        setCurrentItem(item);
    }

    /**
     * Open current item in UI. If current item == null or isBranch, interface will be cleared from last data.
     */
    public void setCurrentItem(NoticeTreeItem item) {
        editing = false;
        if (item == null || item.isBranch()) {
            noticeArea.setEditable(false);
            noticeArea.setText("");
        } else {
            noticeArea.setEditable(true);
            noticeArea.setText(item.getContent());
        }
        noticeSettingsController.open(item);
        currentTreeItem = item;
        editing = true;
    }

    @FXML
    private void handleContextMenu(ActionEvent event) {
        Object source = event.getSource();
        if (source == addBranchItem) {
            handleAddBranch(event);
        } else if (source == addNoticeItem) {
            handleAddNotice(event);
        } else if (source == deleteItem) {
            handleRemoveItem(event);
        }
    }

    @FXML
    private void handleAddNotice(ActionEvent event) {
        noticeTree.addItem(new NoticeTreeItem(tr("notice_name"), "", NoticeStatus.NORMAL), currentTreeItem);
    }

    @FXML
    private void handleAddBranch(ActionEvent event) {
        noticeTree.addItem(new NoticeTreeItem(tr("branch_name")), currentTreeItem);
    }

    @FXML
    private void handleRemoveItem(ActionEvent event) {
        noticeTree.removeItem(currentTreeItem);
        if (currentTreeItem != null && currentTreeItem.getParent() == null) {
            setCurrentItem(null);
        }
    }

    @FXML
    private void handleNew(ActionEvent event) {
        fileSaved = null;
        rebuildTree(tr("help_msg"));
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

            noticeTree = AutoFormat.open(fileSaved);
            noticeTreeView.setRoot(noticeTree.getRoot());
            saved.set(true); // open from FS - already saved
            setCurrentItem(null);
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
            processExport(Exporter.JSON.setup(file, noticeTree));
        } else {
            processExport(Exporter.ZIP.setup(file, noticeTree));
        }
    }

    @FXML
    private void handleExportHtml(ActionEvent event) {
        File destDir = Chooser.directory()
                .title("Select directory to save HTML files")
                .show();
        if (destDir == null) return;
        processExport(Exporter.HTML.setup(destDir, noticeTree));
    }

    @FXML
    private void handleImportFromWeb(ActionEvent event) {
        if (currentTreeItem == null || currentTreeItem.isBranch()) {
            SimpleAlert.info(tr("select_notice"));
            return;
        }
        importHtmlWindow.showAndWait((html, error) -> {
            importHtmlWindow.close();
            if (html == null) {
                SimpleAlert.error(tr("loading_error"), error);
            } else {
                noticeArea.setText(html);
            }
        });
    }

    private void processExport(Exporter exporter) {
        toggleWaiting(true);
        exporter.export(error -> {
            toggleWaiting(false);
            if (error == null) {
                saved.set(true);
                SimpleAlert.info(tr("saved"));
            } else {
                SimpleAlert.error(tr("save_error"), error);
            }
            return null;
        });
    }

    @FXML
    private void handleExit(ActionEvent event) {
        SimpleAlert.confirm(tr("exit_alert")).ifPresent(btn -> {
            if (btn == ButtonType.APPLY) Platform.exit();
        });
    }

    @FXML
    private void handleSwitchOrientation(ActionEvent event) {
        editorPanel.setOrientation(editorPanel.getOrientation() == Orientation.HORIZONTAL
                ? Orientation.VERTICAL : Orientation.HORIZONTAL);
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
        System.out.println("Window title was updated");
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
