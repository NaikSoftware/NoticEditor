package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.model.Attached;
import com.temporaryteam.treenote.model.NoticeStatus;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.view.Chooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class. Responsible for attaching files and note importance.
 *
 * @author Naik
 */
public class NoticeSettingsController implements Initializable {

    @FXML
    private GridPane settingsPane;
    @FXML
    private ListView<Attached> listAttached;
    @FXML
    private ChoiceBox<NoticeStatus> choiceBoxNoticeStatus;

    private MainController mainController;
    private ResourceBundle res;
    private final ObservableList emptyList = FXCollections.emptyObservableList();
    private NoticeTreeItem openedItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.res = rb;
        choiceBoxNoticeStatus.setItems(FXCollections.observableArrayList(NoticeStatus.values()));
        open(null);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void open(NoticeTreeItem item) {
        if (openedItem != null && openedItem.isLeaf()) {
            choiceBoxNoticeStatus.valueProperty().unbindBidirectional(openedItem.statusProperty());
        }
        if (item == null || item.isBranch()) {
            choiceBoxNoticeStatus.getSelectionModel().clearSelection();
            listAttached.setItems(emptyList);
            settingsPane.setDisable(true);
        } else {
            choiceBoxNoticeStatus.valueProperty().bindBidirectional(item.statusProperty());
            listAttached.setItems(item.getAttachesForDisplay());
            settingsPane.setDisable(false);
        }
        openedItem = item;
    }

    @FXML
    private void handleRemoveAttach(ActionEvent event) {
        Attached toRemove = listAttached.getSelectionModel().getSelectedItem();
        if (toRemove != null) {
            openedItem.removeAttach(toRemove);
        }
    }

    @FXML
    private void handleSelectAttach(ActionEvent event) {
        File file = Chooser.file().filter(Chooser.ALL).title(tr("select_file")).open().show();
        if (file == null) {
            return;
        }
        openedItem.addAttach(new Attached(Attached.State.NEW, file.getAbsolutePath(), file.getName()));
    }

    @FXML
    private void handleSaveFile(ActionEvent event) {

    }

    @FXML
    private void handleInsertInPage(ActionEvent event) {

    }

    private String tr(String key) {
        return res.getString(key);
    }

}
