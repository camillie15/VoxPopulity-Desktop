package com.ug.project.controller;

import com.ug.project.model.User;
import com.ug.project.repository.UserDAO;
import com.ug.project.ui.LoginApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField txtName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister(ActionEvent e) {
        String name = txtName.getText();
        String last = txtLastName.getText();
        String email = txtEmail.getText();
        String usern = txtUsername.getText();
        String pass = txtPassword.getText();

        if (name.isBlank() || last.isBlank() || email.isBlank() || usern.isBlank() || pass.isBlank()) {
            alert(Alert.AlertType.WARNING, "Completa todos los campos.");
            return;
        }
        if (!email.contains("@")) {
            alert(Alert.AlertType.WARNING, "Email inválido.");
            return;
        }

        User u = new User();
        u.setName(name);
        u.setLastName(last);
        u.setEmail(email);
        u.setUsername(usern);
        u.setPassword(pass);
        u.setRol(1);

        try {
            boolean ok = userDAO.register(u);
            if (ok) {
                alert(Alert.AlertType.INFORMATION, "Cuenta creada. ¡Inicia sesión!");
                LoginApp.switchTo("/com/ug/project/ui/Login.fxml", 360, 260, "Login - VoxPopuliDB");
            } else {
                alert(Alert.AlertType.ERROR, "No se pudo registrar. Intenta de nuevo.");
            }
        } catch (IllegalStateException dup) {
            alert(Alert.AlertType.ERROR, dup.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            alert(Alert.AlertType.ERROR, "Error inesperado al registrar.");
        }
    }

    @FXML
    private void onBack(ActionEvent e) {
        LoginApp.switchTo("/com/ug/project/ui/Login.fxml", 360, 260, "Login - VoxPopuliDB");
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
