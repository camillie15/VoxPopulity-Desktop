package com.ug.project;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.repository.TestConnection;
import com.ug.project.ui.LoginApp;
import javafx.application.Application;

public class ProjectValidation {

    public static void main(String[] args) {
        try {
            JPAUtil jpa = new JPAUtil();
            TestConnection testConnection = new TestConnection();
            testConnection.testConnection();
        } catch (Throwable ex) {
            System.err.println("Advertencia: no se pudo inicializar JPA antes de arrancar JavaFX: " + ex);
        }

        Application.launch(LoginApp.class, args);
    }
}
