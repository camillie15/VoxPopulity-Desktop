package com.ug.project.ui;

import javafx.application.Application;

import com.ug.project.infrastructure.JPAUtil;

/**
 * Lanzador principal que no extiende Application. Evita problemas de carga
 * cuando la clase principal necesita inicializar recursos previos al inicio de JavaFX.
 */
public class App {

    public void runApp (String[] args) {
        Application.launch(LoginApp.class, args);
    }

}

