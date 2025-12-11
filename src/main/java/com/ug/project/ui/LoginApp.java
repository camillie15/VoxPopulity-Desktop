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
        // Asegurar un tamaño inicial cómodo para la aplicación (evitar ventanas muy pequeñas)
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        // Evitar que la ventana sea demasiado pequeña (min size)
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        switchTo("/com/ug/project/ui/Login.fxml", 1200, 800, "Login - VoxPopuliDB");
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
            // Si el primaryStage ya tiene un tamaño establecido, lo reutilizamos
            double width = (primaryStage != null && primaryStage.getWidth() > 0) ? primaryStage.getWidth() : w;
            double height = (primaryStage != null && primaryStage.getHeight() > 0) ? primaryStage.getHeight() : h;
            Scene scene = new Scene(root, width, height);
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
            double width = (primaryStage != null && primaryStage.getWidth() > 0) ? primaryStage.getWidth() : w;
            double height = (primaryStage != null && primaryStage.getHeight() > 0) ? primaryStage.getHeight() : h;
            Scene scene = new Scene(loader.load(), width, height);
            applyStyle(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.setScene(scene);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar FXML: " + fxmlPath, ex);
        }
    }

}
