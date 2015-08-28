package com.temporaryteam.treenote;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "false");
        primaryStage.setTitle("TreeNote");
        ResourceBundle res = ResourceBundle.getBundle("resources.translate.Language", Locale.getDefault());
        Context.init(res, primaryStage);
        Context.loadToStage("Main").show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
