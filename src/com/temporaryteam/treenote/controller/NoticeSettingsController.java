package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.model.Attached;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.view.Chooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    private ChoiceBox<String> choiceBoxNoticeStatus;

    private MainController mainController;
    private ResourceBundle res;
    private final Stage stage;
    private final ObservableList emptyList = FXCollections.emptyObservableList();

    public NoticeSettingsController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.res = rb;
        choiceBoxNoticeStatus.setItems(FXCollections.observableArrayList(
                rb.getString("normal"), rb.getString("important")
        ));
        choiceBoxNoticeStatus.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                NoticeTreeItem currentNotice = mainController.getCurrentNotice();
                if (currentNotice != null && currentNotice.isLeaf()) {
                    currentNotice.setStatus(newValue.intValue());
                }
            }
        });
        open(null);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void open(NoticeTreeItem item) {
        if (item == null || item.isBranch()) {
            choiceBoxNoticeStatus.getSelectionModel().clearSelection();
            listAttached.setItems(emptyList);
            settingsPane.setDisable(true);
        } else {
            choiceBoxNoticeStatus.getSelectionModel().select(item.getStatus());
            listAttached.setItems(item.getAttachesForDisplay());
            settingsPane.setDisable(false);
        }
    }

    @FXML
    private void handleRemoveAttach(ActionEvent event) {
        Attached toRemove = listAttached.getSelectionModel().getSelectedItem();
        if (toRemove != null) {
            if (toRemove.getState() == Attached.State.NEW) {
                mainController.getCurrentNotice().getAttaches().remove(toRemove);
            } else {
                toRemove.changeState(Attached.State.REMOVED);
                listAttached.setItems(mainController.getCurrentNotice().getAttachesForDisplay());
            }
        }
    }

    @FXML
    private void handleSelectAttach(ActionEvent event) {
        File file = Chooser.file().filter(Chooser.ALL).title(tr("select_file")).open().show(stage);
        if (file == null) {
            return;
        }
        mainController.getCurrentNotice().addAttach(
                new Attached(Attached.State.NEW, file.getAbsolutePath(), file.getName()));
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
