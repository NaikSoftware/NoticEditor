package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author Naik
 */
public class SimpleAlert extends Alert {

    private static SimpleAlert simpleAlert;

    public static Optional<ButtonType> confirm(String header) {
        SimpleAlert alert = getInstance(AlertType.CONFIRMATION, ButtonType.APPLY, ButtonType.NO);
        alert.setHeaderText(header);
        return alert.showAndWait();
    }

    public static void info(String header) {
        SimpleAlert alert = getInstance(AlertType.INFORMATION, ButtonType.APPLY);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static Optional<String> input(String header, String text) {
        SimpleAlert alert = getInstance(AlertType.NONE, ButtonType.OK);
        alert.setHeaderText(header);
        alert.getDialogPane().setExpandableContent(alert.expContent);
        alert.getDialogPane().setExpanded(true);
        alert.expTextField.setText(text);
        alert.expContent.add(alert.expTextField, 0, 0);
        return alert.showAndWait().map(btn -> alert.expTextField.getText());
    }

    public static void error(String header, Exception exception) {
        SimpleAlert alert = getInstance(AlertType.ERROR, ButtonType.APPLY);
        alert.setHeaderText(header);

        alert.getDialogPane().setExpandableContent(alert.expContent);
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        alert.expTextArea.setText(sw.toString());
        alert.expContent.add(alert.expTextArea, 0, 0);
        alert.showAndWait();
    }

    private static SimpleAlert getInstance(AlertType type, ButtonType... buttons) {
        if (simpleAlert == null) simpleAlert = new SimpleAlert();
        simpleAlert.setAlertType(type);
        simpleAlert.getDialogPane().setExpandableContent(null);
        simpleAlert.getButtonTypes().clear();
        simpleAlert.getButtonTypes().addAll(buttons);
        simpleAlert.expContent.getChildren().clear();
        return simpleAlert;
    }

    public final TextArea expTextArea;
    public final TextField expTextField;
    public final GridPane expContent;

    public SimpleAlert() {
        super(AlertType.NONE);
        initOwner(Context.getPrimaryStage());

        expTextArea = new TextArea();
        expTextArea.setEditable(false);
        expTextArea.setMaxWidth(Double.MAX_VALUE);
        expTextArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(expTextArea, Priority.ALWAYS);
        GridPane.setHgrow(expTextArea, Priority.ALWAYS);

        expTextField = new TextField();
        expTextField.setMaxWidth(Double.MAX_VALUE);
        GridPane.setVgrow(expTextField, Priority.ALWAYS);
        GridPane.setHgrow(expTextField, Priority.NEVER);

        expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
    }

}
