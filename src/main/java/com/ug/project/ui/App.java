package com.ug.project.ui;

import javafx.application.Application;

import com.ug.project.infrastructure.JPAUtil;

/**
 * Lanzador principal que no extiende Application. Evita problemas de carga
 * cuando la clase principal necesita inicializar recursos previos al inicio de JavaFX.
 */
public class App {

    public static void main(String[] args) {
        // Inicializar recursos que queramos preparar antes de iniciar JavaFX
        try {
            new JPAUtil(); // inicializa EntityManagerFactory temprano (opcional)
        } catch (Throwable ex) {
            System.err.println("Advertencia: no se pudo inicializar JPA antes de arrancar JavaFX: " + ex);
        }

        // Iniciar la aplicación JavaFX delegando en LoginApp
        Application.launch(LoginApp.class, args);
    }
}

