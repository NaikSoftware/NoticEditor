package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.importer.WebImporter;
import com.temporaryteam.treenote.io.ExportException;
import com.temporaryteam.treenote.io.ExportStrategy;
import com.temporaryteam.treenote.io.ExportStrategyHolder;
import com.temporaryteam.treenote.io.DocumentFormat;
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

import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.model.PreviewStyles;
import com.temporaryteam.treenote.view.Chooser;
import com.temporaryteam.treenote.view.EditNoticeTreeCell;
import com.temporaryteam.treenote.view.SimpleAlert;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

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
	private ProgressBar progressBar;
	
	@FXML
	private SplitPane mainPane;

	@FXML
	private ResourceBundle resources; // magic!

	private final Stage primaryStage;
	private WebEngine engine;
	private final PegDownProcessor processor;
	private NoticeTree noticeTree;
	private NoticeTreeItem currentTreeItem;
	private File fileSaved;
	private NoticeSettingsController noticeSettingsController;

	public NoticeController(Stage stage) {
		this.primaryStage = stage;
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
		engine = viewer.getEngine();
		progressBar.managedProperty().bind(progressBar.visibleProperty()); // for hide progress bar

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
		rebuildTree(resources.getString("help_msg"));
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
					.show(primaryStage);
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
				.show(primaryStage);
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
		toggleWaiting(true);
		DocumentFormat.save(file, noticeTree, strategy, (error) -> {
			toggleWaiting(false);
			if (error == null) {
				new SimpleAlert(resources.getString("saved"), primaryStage).showAndWait();
			} else {
				new SimpleAlert(error, resources.getString("save_error"), primaryStage).showAndWait();
			}
			return null;
		});
	}

	@FXML
	private void handleExportHtml(ActionEvent event) {
		File destDir = Chooser.directory()
				.title("Select directory to save HTML files")
				.show(primaryStage);
		if (destDir == null)
			return;

		toggleWaiting(true);
		try {
			ExportStrategyHolder.HTML.setProcessor(processor);
			ExportStrategyHolder.HTML.export(destDir, noticeTree);
			new SimpleAlert(resources.getString("export_success"), primaryStage).showAndWait();
		} catch (ExportException e) {
			new SimpleAlert(e, resources.getString("save_error"), primaryStage).showAndWait();
		}
		toggleWaiting(false);
	}

	@FXML
	private void handleImportFromWeb(ActionEvent event) {
		if (currentTreeItem == null || currentTreeItem.isBranch()) {
			new SimpleAlert(resources.getString("select_notice"), primaryStage).showAndWait();
			return;
		}
		TextInputDialog dialog = new TextInputDialog("http://");
		dialog.setHeaderText(resources.getString("input_url"));
		dialog.initOwner(primaryStage);
		dialog.showAndWait().ifPresent((url) -> {
			toggleWaiting(true);
			WebImporter.from(dialog.getEditor().getText()).grab((pair) -> {
				toggleWaiting(false);
				String data = pair.getValue();
				if (data == null) {
					new SimpleAlert(pair.getKey(), resources.getString("loading_error"), primaryStage).showAndWait();
				} else {
					noticeArea.setText(pair.getValue());
				}
				return null;
			});
		});
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

	private void toggleWaiting(boolean wait) {
		progressBar.setVisible(wait);
		mainPane.setDisable(wait);
	}

}
