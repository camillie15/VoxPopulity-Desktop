package com.ug.project.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        switchTo("/com/ug/project/ui/Login.fxml", 360, 260, "Login - VoxPopuliDB");
        primaryStage.show();
    }

    public static void switchTo(String fxmlPath, int w, int h, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), w, h);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args); }
}
