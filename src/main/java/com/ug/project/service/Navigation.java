package com.ug.project.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import javafx.stage.Window;

public class Navigation {

    /**
     *CAMBIAR ESCENA (Navegación normal)
     * Cierra el contenido actual y carga uno nuevo en la misma ventana.
     * @param event el evento al cual está asociado la función que llamamos
     * @param fxmlPath path destino del fxml, la vista de la escena
     * @param title título que tendrá la ventana
     */
    public static void switchScene(ActionEvent event, String fxmlPath, String title){
        try {

            URL url = Navigation.obtainUrl(fxmlPath);
            if (url == null) {
                return;
            }
            // 1. Cargar el FXML
            Parent root = FXMLLoader.load(url);
            // 2. Obtener el Stage actual desde el evento (el botón presionado)
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            // Reutilizar el tamaño actual de la ventana para la nueva escena
            double w = stage.getWidth() <= 0 ? 1000 : stage.getWidth();
            double h = stage.getHeight() <= 0 ? 600 : stage.getHeight();
            // 3. Cambiar la escena manteniendo el tamaño
            Scene scene = new Scene(root, w, h);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e){
            System.err.println("Error al cargar la vista: " + fxmlPath);
            System.err.println("Error: " + e);
        }
    }

    /**
     * ABRIR NUEVO STAGE (Ventana emergente/Pop-up)
     * Abre una ventana nueva encima de la anterior.
     * @param fxmlPath path destino del fxml, la vista de la escena
     * @param title título que tendrá la ventana
     */
    public static void openNewScreen(String fxmlPath, String title) {
        try {

            URL url = Navigation.obtainUrl(fxmlPath);
            if (url == null) {
                return;
            }

            // 1. Cargar el FXML
            Parent root = FXMLLoader.load(url);
            //2. Crea una nueva escena
            // Intentar usar el tamaño de la ventana actualmente activa (si existe)
            Window active = Window.getWindows().stream().filter(Window::isShowing).filter(Window::isFocused).findFirst()
                    .orElse(Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
            double w = 1200, h = 800;
            if (active != null) {
                w = active.getWidth() <= 0 ? w : active.getWidth();
                h = active.getHeight() <= 0 ? h : active.getHeight();
            }
            Scene scene = new Scene(root, w, h);
            //3. Crea el nuevo stage y carga la escena anterior aquí
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.centerOnScreen();
            // Bloquear la ventana de atrás hasta cerrar esta (Modal)
            newStage.initModality(Modality.APPLICATION_MODAL);
            //4. Muestra la nueva ventana
            newStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana: " + fxmlPath);
            System.err.println("Error: " + e);

        }
    }

    /**
     * VERIFICAR PATH DEL FXML
     * Comprueba y devuelve el url del fxmPath
     * @param fxmlPath path destino del fxml, la vista de la escena
     * @return URL
     */
    private static URL obtainUrl  (String fxmlPath) {
        URL url = Navigation.class.getResource(fxmlPath);
        if (url == null) {
            System.err.println("ERROR FATAL: No se encontró el archivo FXML: " + fxmlPath);
            System.err.println("Verifica que la ruta empiece con '/' y el archivo esté en 'resources'.");
            return null;
        }
        return url;
    }

}
