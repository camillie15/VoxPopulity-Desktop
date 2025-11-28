// src/main/java/com/ug/project/controller/LoginController.java
package com.ug.project.controller;

import com.ug.project.model.User;
import com.ug.project.repository.UserDAO;
import com.ug.project.service.Navigation;
import com.ug.project.ui.LoginApp;
import com.ug.project.infrastructure.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void onLogin(ActionEvent e) {
        String u = txtUsername.getText();
        String p = txtPassword.getText();

        if (u == null || u.isBlank() || p == null || p.isBlank()) {
            alert(Alert.AlertType.WARNING, "Completa usuario y contraseña.");
            return;
        }

        User user = userDAO.login(u, p);
        if (user != null) {
            // Guardar sesión del usuario
            SessionManager.setCurrentUser(user);
            // navegar a la pantalla de comunidades
            // Abrir Comunidades con tamaño mayor (estilo SPA)
            Navigation.switchScene(e, "/com/ug/project/ui/Dashboard.fxml", "VoxPopuly");
        } else {
            alert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void onGoRegister(ActionEvent e) {
        LoginApp.switchTo("/com/ug/project/ui/Register.fxml", 420, 380, "Crear cuenta");
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
