package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Naik
 */
public class SimpleAlert extends Alert {

    private static SimpleAlert simpleAlert;

    public static void info(String header) {
        SimpleAlert alert = getInstance();
        alert.setAlertType(AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.getDialogPane().setExpandableContent(null);
        alert.showAndWait();
    }

    public static void error(String header, Exception exception) {
        SimpleAlert alert = getInstance();
        alert.setAlertType(AlertType.ERROR);
        alert.setHeaderText(header);

        alert.getDialogPane().setExpandableContent(alert.expContent);
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        alert.errorTextArea.setText(sw.toString());
        alert.showAndWait();
    }

    private static SimpleAlert getInstance() {
        if (simpleAlert == null) simpleAlert = new SimpleAlert();
        return simpleAlert;
    }

    public final TextArea errorTextArea;
    public final GridPane expContent;

    public SimpleAlert() {
        super(AlertType.NONE);
        initOwner(Context.getPrimaryStage());

        errorTextArea = new TextArea();
        errorTextArea.setEditable(false);

        errorTextArea.setMaxWidth(Double.MAX_VALUE);
        errorTextArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(errorTextArea, Priority.ALWAYS);
        GridPane.setHgrow(errorTextArea, Priority.ALWAYS);

        expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(errorTextArea, 0, 1);
    }

}
