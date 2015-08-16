package com.temporaryteam.treenote.controller;

import com.temporaryteam.treenote.importer.WebImporter;
import com.temporaryteam.treenote.io.ExportException;
import com.temporaryteam.treenote.io.ExportStrategy;
import com.temporaryteam.treenote.io.ExportStrategyHolder;
import com.temporaryteam.treenote.io.DocumentFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.json.JSONException;

import org.pegdown.PegDownProcessor;
import static org.pegdown.Extensions.*;

import java.io.File;
import java.io.IOException;

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
import com.temporaryteam.treenote.view.EditableTreeCell;
import com.temporaryteam.treenote.view.ImportHtmlDialog;
import com.temporaryteam.treenote.view.SimpleAlert;
import java.util.ResourceBundle;
import javafx.stage.Stage;

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

	private final Stage primaryStage;
	private WebEngine engine;
	private final PegDownProcessor processor;
	private NoticeTree noticeTree;
	private NoticeTreeItem currentTreeItem;
	private File fileSaved;

	public MainController(Stage stage) {
		this.primaryStage = stage;
		processor = new PegDownProcessor(AUTOLINKS | TABLES | FENCED_CODE_BLOCKS);
	}

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private void initialize() {
        noticeSettingsController.setMainController(this);
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
		noticeTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            currentTreeItem = (NoticeTreeItem) newVal;
            open();
        });
		noticeTreeView.setCellFactory(treeView ->  new EditableTreeCell());

		noticeArea.textProperty().addListener((observable, oldVal, newVal) -> {
            engine.loadContent(processor.markdownToHtml(newVal));
            if (currentTreeItem != null) {
                currentTreeItem.changeContent(newVal);
            }
		});
		noticeArea.wrapTextProperty().bind(wordWrapItem.selectedProperty());

		hideEditorMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            editorPanel.getDividers().get(0).setPosition(newValue ? 0 : 0.5);
		});

		rebuildTree(tr("help_msg"));
	}

	public void rebuildTree(String defaultNoticeContent) {
		noticeTree = new NoticeTree(new NoticeTreeItem("Root"));
		currentTreeItem = new NoticeTreeItem(tr("notice_name"), defaultNoticeContent, NoticeTreeItem.STATUS_NORMAL);
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
		noticeTree.addItem(new NoticeTreeItem(tr("notice_name"), "", NoticeTreeItem.STATUS_NORMAL), currentTreeItem);
	}
	
	@FXML
	private void handleAddBranch(ActionEvent event) {
		noticeTree.addItem(new NoticeTreeItem(tr("branch_name")), currentTreeItem);
	}
	
	@FXML
	private void handleRemoveItem(ActionEvent event) {
		noticeTree.removeItem(currentTreeItem);
		if (currentTreeItem != null && currentTreeItem.getParent() == null) {
			currentTreeItem = null;
			noticeSettingsController.open(null);
		}
	}
	

	@FXML
	private void handleNew(ActionEvent event) {
		rebuildTree(tr("help_msg"));
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
			new SimpleAlert(e, tr("open_error"), primaryStage).showAndWait();
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
				new SimpleAlert(tr("saved"), primaryStage).showAndWait();
			} else {
				new SimpleAlert(error, tr("save_error"), primaryStage).showAndWait();
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
			new SimpleAlert(tr("export_success"), primaryStage).showAndWait();
		} catch (ExportException e) {
			new SimpleAlert(e, tr("save_error"), primaryStage).showAndWait();
		}
		toggleWaiting(false);
	}

	@FXML
	private void handleImportFromWeb(ActionEvent event) {
		if (currentTreeItem == null || currentTreeItem.isBranch()) {
			new SimpleAlert(tr("select_notice"), primaryStage).showAndWait();
			return;
		}
		ImportHtmlDialog dialog = new ImportHtmlDialog(primaryStage, tr("input_url"), resources);
		dialog.showAndWait().ifPresent((url) -> {
			toggleWaiting(true);
			WebImporter.from(url).mode(dialog.getSelectedMode()).grab((result) -> {
				toggleWaiting(false);
				if (result instanceof Exception) {
					new SimpleAlert((Exception) result, tr("loading_error"), primaryStage).showAndWait();
				} else {
					noticeArea.setText(result.toString());
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
        try {
            Stage stage = new Stage();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/InfoFrame.fxml"), resources));
            stage.setScene(scene);
            stage.initOwner(primaryStage);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public NoticeTreeItem getCurrentNotice() {
		return currentTreeItem;
	}

	private void toggleWaiting(boolean wait) {
		progressBar.setVisible(wait);
		mainPane.setDisable(wait);
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
