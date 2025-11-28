package com.ug.project.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.function.Consumer;

public class LoginApp extends Application {
    private static Stage primaryStage;
    private static final Logger LOGGER = Logger.getLogger(LoginApp.class.getName());

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        switchTo("/com/ug/project/ui/Login.fxml", 360, 260, "Login - VoxPopuliDB");
        stage.centerOnScreen();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private static void applyStyle(Scene scene) {
        try {
            java.net.URL url = LoginApp.class.getResource("/com/ug/project/ui/style.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            } else {
                LOGGER.log(Level.WARNING, "No se encontró style.css en recursos");
            }
        } catch (Exception ex) {
            // si falla, no rompemos la app
            LOGGER.log(Level.WARNING, "No se pudo cargar stylesheet global", ex);
        }
    }

    public static void styleScene(Scene scene) {
        applyStyle(scene);
    }

    /**
     * Cambia la escena principal y permite ejecutar un inicializador sobre el controlador cargado.
     * Esto facilita la navegación SPA y pasar datos a controladores.
     */
    public static <T> void switchToWithInit(String fxmlPath, int w, int h, String title, Consumer<T> initializer) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(LoginApp.class.getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            @SuppressWarnings("unchecked")
            T controller = (T) loader.getController();
            if (initializer != null && controller != null) initializer.accept(controller);
            Scene scene = new Scene(root, w, h);
            applyStyle(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.setScene(scene);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar FXML con init: " + fxmlPath, ex);
        }
    }

    public static void switchTo(String fxmlPath, int w, int h, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), w, h);
            applyStyle(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.setScene(scene);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar FXML: " + fxmlPath, ex);
        }
    }

}
