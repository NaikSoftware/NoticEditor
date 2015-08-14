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
	private Button btnRemoveFile;
	@FXML
	private Button btnSelectFile;
	@FXML
	private ChoiceBox<String> choiceBoxNoticeStatus;

	private final NoticeController noticeController;
	private ResourceBundle res;
	private final Stage stage;

	public NoticeSettingsController(NoticeController noticeController, Stage stage) {
		this.noticeController = noticeController;
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
				NoticeTreeItem currentNotice = noticeController.getCurrentNotice();
				if (currentNotice != null && currentNotice.isLeaf()) {
					currentNotice.setStatus(newValue.intValue());
				}
			}
		});
		open(null);
	}

	public void open(NoticeTreeItem item) {
		if (item == null || item.isBranch()) {
			choiceBoxNoticeStatus.getSelectionModel().clearSelection();
			listAttached.getSelectionModel().clearSelection();
			settingsPane.setDisable(true);
		} else {
			choiceBoxNoticeStatus.getSelectionModel().select(item.getStatus());
			listAttached.setItems(item.getAttaches());
			settingsPane.setDisable(false);
		}
	}

	@FXML
	private void handleRemoveAttach(ActionEvent event) {
		Attached toRemove = listAttached.getSelectionModel().getSelectedItem();
		if (toRemove != null) {
			noticeController.getCurrentNotice().getAttaches().remove(toRemove);
		}
	}

	@FXML
	private void handleSelectAttach(ActionEvent event) {
		File file = Chooser.file().title(tr("select_file")).open().show(stage);
		if (file == null) {
			return;
		}
		noticeController.getCurrentNotice().addAttach(
				new Attached(Attached.State.NEW, file.getAbsolutePath(), file.getName()));
	}

	private String tr(String key) {
		return res.getString(key);
	}

}
