package com.ug.project.ui;

import com.ug.project.controller.PostController;
import com.ug.project.infrastructure.PostContext;
import com.ug.project.model.Post;
import com.ug.project.service.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditPostUI implements Initializable{

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Post postToEdit;
    private final PostController postController = new PostController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Obtener el post del contexto
        postToEdit = PostContext.getInstance().getCurrentPost();

        if (postToEdit != null) {
            loadPostData();
        } else {
            System.err.println("Error: No se encontró post para editar");
            closeWindow();
        }

        setupEventHandlers();
    }

    private void loadPostData() {
        titleField.setText(postToEdit.getTitle());
        contentArea.setText(postToEdit.getContent());
    }

    private void setupEventHandlers() {
        saveButton.setOnAction(this::handleSave);
        cancelButton.setOnAction(this::handleCancel);
    }

    private void handleSave(ActionEvent event) {
        try {
            // Validar campos
            if (titleField.getText().trim().isEmpty()) {
                showAlert("Error", "El título no puede estar vacío");
                return;
            }

            if (contentArea.getText().trim().isEmpty()) {
                showAlert("Error", "El contenido no puede estar vacío");
                return;
            }

            // Actualizar el post
            postToEdit.setTitle(titleField.getText().trim());
            postToEdit.setContent(contentArea.getText().trim());

            // Llamar al controlador para guardar
            boolean updated = postController.editPost(postToEdit);

            if (updated) {
                // Limpiar el contexto
                PostContext.getInstance().clearCurrentPost();

                // Cerrar ventana
                closeWindow();
                showAlert("Éxito", "Post actualizado correctamente");

                // Nota: La actualización de la lista principal se manejará
                // cuando el usuario regrese a la ventana de posts
            } else {
                showAlert("Error", "No se pudo actualizar el post");
            }

        } catch (Exception e) {
            showAlert("Error", "Error al actualizar: " + e.getMessage());
        }
    }

    private void handleCancel(ActionEvent event) {
        // Limpiar el contexto al cancelar
        PostContext.getInstance().clearCurrentPost();
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
