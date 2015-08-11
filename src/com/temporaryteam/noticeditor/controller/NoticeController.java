package com.temporaryteam.noticeditor.controller;

import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.io.File;
import java.io.IOException;

import javafx.util.Callback;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import com.temporaryteam.noticeditor.Main;
import com.temporaryteam.noticeditor.io.*;
import com.temporaryteam.noticeditor.model.NoticeTree;
import com.temporaryteam.noticeditor.model.NoticeTreeItem;
import com.temporaryteam.noticeditor.model.PreviewStyles;
import com.temporaryteam.noticeditor.view.Chooser;
import com.temporaryteam.noticeditor.view.EditNoticeTreeCell;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import jfx.messagebox.MessageBox;

public class NoticeController {

	private static final Logger logger = Logger.getLogger(NoticeController.class.getName());

	@FXML
	private SplitPane editorPanel;

	@FXML
	private TextArea noticeArea;

	@FXML
	private WebView viewer;

	@FXML
	private MenuItem addBranchItem, addNoticeItem, deleteItem;

	@FXML
	private CheckMenuItem wordWrapItem;

	@FXML
	private Menu previewStyleMenu;

	@FXML
	private TreeView<String> noticeTreeView;

	@FXML
	private ResourceBundle resources; // magic!

	private final Main main;
	private WebEngine engine;
	private final PegDownProcessor processor;
	private NoticeTree noticeTree;
	private NoticeTreeItem currentTreeItem;
	private File fileSaved;
	private NoticeSettingsController noticeSettingsController;

	public NoticeController(Main main) {
		this.main = main;
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		engine = viewer.getEngine();

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

		noticeTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		noticeTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
				currentTreeItem = (NoticeTreeItem) newValue;
				open();
			}
		});
		noticeTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			@Override
			public TreeCell<String> call(TreeView<String> p) {
				return new EditNoticeTreeCell();
			}
		});

		noticeArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				engine.loadContent(processor.markdownToHtml(newValue));
				if (currentTreeItem != null) {
					currentTreeItem.changeContent(newValue);
				}
			}
		});
		noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());
		rebuildTree(resources.getString("help"));
	}

	/**
	 * Rebuild tree
	 */
	public void rebuildTree(String defaultNoticeContent) {
		noticeTree = new NoticeTree(new NoticeTreeItem("Root"));
		currentTreeItem = new NoticeTreeItem("Default notice", defaultNoticeContent, NoticeTreeItem.STATUS_NORMAL);
		noticeTree.addItem(currentTreeItem, noticeTree.getRoot());
		noticeTreeView.setRoot(noticeTree.getRoot());
		open();
	}

	/**
	 * Open current item in UI. If current item == null or isBranch, interface will be cleared from last data.
	 */
	public void open() {
		if (currentTreeItem == null || currentTreeItem.isBranch()) {
			noticeArea.setEditable(false);
			noticeArea.setText("");
		} else {
			noticeArea.setEditable(true);
			noticeArea.setText(currentTreeItem.getContent());
		}
		noticeSettingsController.open(currentTreeItem);
	}

	/**
	 * Handler
	 */
	@FXML
	private void handleContextMenu(ActionEvent event) {
		Object source = event.getSource();
		if (source == addBranchItem) {
			noticeTree.addItem(new NoticeTreeItem("New branch"), currentTreeItem);
		} else if (source == addNoticeItem) {
			noticeTree.addItem(new NoticeTreeItem("New notice", "", NoticeTreeItem.STATUS_NORMAL), currentTreeItem);
		} else if (source == deleteItem) {
			noticeTree.removeItem(currentTreeItem);
			if (currentTreeItem != null && currentTreeItem.getParent() == null) {
				currentTreeItem = null;
				noticeSettingsController.open(null);
			}
		}
	}

	@FXML
	private void handleNew(ActionEvent event) {
		rebuildTree(resources.getString("help"));
		fileSaved = null;
	}

	@FXML
	private void handleOpen(ActionEvent event) {
		try {
			fileSaved = Chooser.file().open()
					.filter(Chooser.SUPPORTED, Chooser.ALL)
					.title("Open notice")
					.show(main.getPrimaryStage());
			if (fileSaved == null) {
				return;
			}

			noticeTree = DocumentFormat.open(fileSaved);
			noticeTreeView.setRoot(noticeTree.getRoot());
			currentTreeItem = null;
			open();
		} catch (IOException | JSONException e) {
			logger.log(Level.SEVERE, null, e);
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
				.show(main.getPrimaryStage());
		if (fileSaved == null)
			return;

		saveDocument(fileSaved);
	}

	private void saveDocument(File file) {
		ExportStrategy strategy;
		if (Chooser.JSON.equals(Chooser.getLastSelectedExtensionFilter())
				|| file.getName().toLowerCase().endsWith(".json")) {
			strategy = ExportStrategyHolder.JSON;
		} else {
			strategy = ExportStrategyHolder.ZIP;
		}
		DocumentFormat.save(file, noticeTree, strategy, new SaveListener() {

			@Override
			public void onComplete() {
				MessageBox.show(main.getPrimaryStage(), resources.getString("saved"), "", MessageBox.ICON_INFORMATION);
			}

			@Override
			public void onError(ExportException ex) {
				MessageBox.show(main.getPrimaryStage(), ex.getLocalizedMessage(),
						resources.getString("save_error"), MessageBox.ICON_ERROR);
			}
		});
	}

	@FXML
	private void handleExportHtml(ActionEvent event) {
		File destDir = Chooser.directory()
				.title("Select directory to save HTML files")
				.show(main.getPrimaryStage());
		if (destDir == null)
			return;

		try {
			ExportStrategyHolder.HTML.setProcessor(processor);
			ExportStrategyHolder.HTML.export(destDir, noticeTree);
			MessageBox.show(main.getPrimaryStage(), resources.getString("export_success"), "", MessageBox.ICON_INFORMATION);
		} catch (ExportException e) {
			logger.log(Level.SEVERE, null, e);
			MessageBox.show(main.getPrimaryStage(), e.getLocalizedMessage(),
					resources.getString("save_error"), MessageBox.ICON_ERROR);
		}
	}

	@FXML
	private void handleExit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	private void handleSwitchOrientation(ActionEvent event) {
		editorPanel.setOrientation(editorPanel.getOrientation() == Orientation.HORIZONTAL
				? Orientation.VERTICAL : Orientation.HORIZONTAL);
	}

	@FXML
	private void handleAbout(ActionEvent event) {

	}

	public void setNoticeSettingsController(NoticeSettingsController controller) {
		noticeSettingsController = controller;
	}

	public NoticeTreeItem getCurrentNotice() {
		return currentTreeItem;
	}

}
