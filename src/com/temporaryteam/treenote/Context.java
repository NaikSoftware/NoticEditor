package com.temporaryteam.treenote;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Hold primary stage and resource bundle.
 * Used for loading windows from FXML.
 *
 * @author Naik
 */
public class Context {

    public static final Image ICON = new Image("/resources/images/icon.png");
    private static final String FXML_DIR = "/resources/fxml/";
    private static final String EXT = ".fxml";
    private static ResourceBundle resources;
    private static Stage primaryStage;

    public static void init(ResourceBundle r, Stage s) {
        resources = r;
        primaryStage = s;
        primaryStage.getIcons().add(ICON);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Parent loadLayout(String layout_name) {
        try {
            return FXMLLoader.load(getFXML(layout_name), resources);
        } catch (IOException e) {
            throw new RuntimeException("FXML loading error: " + layout_name, e);
        }
    }

    public static Stage loadToNewStage(String layout_name) {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.getIcons().add(ICON);
        return loadToStage(layout_name, stage);
    }

    public static Stage loadToStage(String layout_name) {
        return loadToStage(layout_name, primaryStage);
    }

    private static Stage loadToStage(String layout_name, Stage stage) {
        Scene scene = new Scene(loadLayout(layout_name));
        stage.setScene(scene);
        return stage;
    }

    public static URL getFXML(String layout_name) {
        System.out.println(FXML_DIR + layout_name + EXT);
        return Context.class.getResource(FXML_DIR + layout_name + EXT);
    }

}
