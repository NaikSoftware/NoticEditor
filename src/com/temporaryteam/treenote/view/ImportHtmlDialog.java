package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.controller.ImportHtmlController;
import com.temporaryteam.treenote.importer.WebImporter;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 *
 * @author Naik
 */
public class ImportHtmlDialog extends TextInputDialog {
	
	private ImportHtmlController controller;
	
	public ImportHtmlDialog(Stage stage, String header, ResourceBundle res) {
		super("http://");
		initOwner(stage);
		setHeaderText(header);
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ImportHtmlDialog.fxml"), res);
			getDialogPane().setExpandableContent(loader.load());
			getDialogPane().setExpanded(true);
			controller = loader.getController();
		} catch (IOException ex) {
			throw new RuntimeException("Could not load fxml for ImportHtmlDialog");
		}
	}
	
	public WebImporter.Mode getSelectedMode() {
		return controller.getSelectedMode();
	}
	
}
