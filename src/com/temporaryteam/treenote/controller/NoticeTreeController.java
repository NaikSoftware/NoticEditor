package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.model.Attached;
import com.temporaryteam.treenote.model.NoticeStatus;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.view.Chooser;
import com.temporaryteam.treenote.view.EditableTreeCell;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Naik
 */
public class NoticeTreeController implements Initializable {

    @FXML
    private Parent mainView;
    @FXML
    private TreeView<String> noticeTreeView;
    @FXML
    private MenuItem addBranchItem, addNoticeItem, deleteItem;
    @FXML
    private ListView<Attached> listAttached;
    @FXML
    private ChoiceBox<NoticeStatus> choiceBoxNoticeStatus;

    private ResourceBundle resources;
    private final ObservableList emptyList = FXCollections.emptyObservableList();
    private ObjectProperty<NoticeTreeItem> currentNoticeProperty = new SimpleObjectProperty<>();
    private NoticeTreeItem currentTreeItem;
    private NoticeTree noticeTree;
    private Set<Node> enabledForleaf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        noticeTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noticeTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            setCurrentItem((NoticeTreeItem) newVal);
        });
        noticeTreeView.setCellFactory(treeView -> new EditableTreeCell());
        choiceBoxNoticeStatus.setItems(FXCollections.observableArrayList(NoticeStatus.values()));
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
    private void handleRemoveAttach(ActionEvent event) {
        Attached toRemove = listAttached.getSelectionModel().getSelectedItem();
        if (toRemove != null) {
            currentTreeItem.removeAttach(toRemove);
        }
    }

    @FXML
    private void handleSelectAttach(ActionEvent event) {
        File file = Chooser.file().filter(Chooser.ALL).title(tr("select_file")).open().show();
        if (file == null) {
            return;
        }
        currentTreeItem.addAttach(new Attached(Attached.State.NEW, file.getAbsolutePath(), file.getName()));
    }

    @FXML
    private void handleSaveFile(ActionEvent event) {

    }

    @FXML
    private void handleInsertInPage(ActionEvent event) {

    }

    public void rebuildTree(String defaultNoticeContent) {
        NoticeTree tree = new NoticeTree(new NoticeTreeItem("Root"));
        NoticeTreeItem item = new NoticeTreeItem(tr("notice_name"), defaultNoticeContent, NoticeStatus.NORMAL);
        tree.addItem(item, tree.getRoot());
        openTree(tree);
    }

    public void openTree(NoticeTree noticeTree) {
        this.noticeTree = noticeTree;
        noticeTreeView.setRoot(noticeTree.getRoot());
        setCurrentItem(null);
    }

    /**
     * Open current item in UI. If current item == null or isBranch, interface will be cleared from last data.
     */
    public void setCurrentItem(NoticeTreeItem item) {
        if (currentTreeItem != null && currentTreeItem.isLeaf()) {
            choiceBoxNoticeStatus.valueProperty().unbindBidirectional(currentTreeItem.statusProperty());
        }
        if (item == null || item.isBranch()) {
            choiceBoxNoticeStatus.getSelectionModel().clearSelection();
            listAttached.setItems(emptyList);
            //settingsPane.setDisable(true);
            //noticeArea.setEditable(false);
            //noticeArea.setText("");
        } else {
            choiceBoxNoticeStatus.valueProperty().bindBidirectional(item.statusProperty());
            listAttached.setItems(item.getAttachesForDisplay());
            //settingsPane.setDisable(false);
            //noticeArea.setEditable(true);
            //noticeArea.setText(item.getContent());
        }
        currentNoticeProperty.set(item);
        currentTreeItem = item;
    }

    public NoticeTree getNoticeTree() {
        return noticeTree;
    }

    public ObjectProperty<NoticeTreeItem> currentNoticeProperty() {
        return currentNoticeProperty;
    }

    private String tr(String key) {
        return resources.getString(key);
    }

}
