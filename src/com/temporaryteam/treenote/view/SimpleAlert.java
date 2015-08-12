package com.temporaryteam.treenote.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 *
 * @author Naik
 */
public class SimpleAlert extends Alert {

	public SimpleAlert(String headerText, Stage owner) {
		super(AlertType.INFORMATION);
		initOwner(owner);
		setHeaderText(headerText);
	}

	public SimpleAlert(Exception ex, String headerText, Stage owner) {
		super(AlertType.ERROR);
		initOwner(owner);
		setHeaderText(headerText);
		
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		String exceptionText = sw.toString();

		Label label = new Label("Stacktrace:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		//textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		getDialogPane().setExpandableContent(expContent);
	}

}
