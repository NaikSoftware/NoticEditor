package com.temporaryteam.treenote;

import com.temporaryteam.treenote.controller.MainController;
import com.temporaryteam.treenote.controller.NoticeSettingsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TreeNote");
        initRootLayout();
    }

    /**
     * Initializes root layout
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"),
                    ResourceBundle.getBundle("resources.translate.Language", Locale.getDefault()));
            loader.setControllerFactory((param) -> {
                if (param == MainController.class) {
                    return new MainController(primaryStage);
                } else if (param == NoticeSettingsController.class) {
                    return new NoticeSettingsController(primaryStage);
                }
                return null;
            });
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
