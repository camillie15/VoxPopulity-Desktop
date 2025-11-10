package com.ug.project.controller;

import com.ug.project.model.Community;
import com.ug.project.service.CommunityService;
import com.ug.project.ui.LoginApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class CommunityDetailsController {

    @FXML private Label lblName;
    @FXML private Label lblDescription;
    @FXML private Label lblMembers;

    private Community current;

    public void setCommunity(Community c) {
        this.current = c;
        refresh();
    }

    private void refresh() {
        if (current == null) return;
        lblName.setText(current.getName());
        lblDescription.setText(current.getDescription() == null ? "" : current.getDescription());
        lblMembers.setText("Miembros: " + current.getMembersCount());
    }

    @FXML
    private void onCreatePost() {
        // Punto de entrada para que otro compañero implemente la creación de posts dentro de la comunidad.
        new Alert(Alert.AlertType.INFORMATION, "Punto de entrada: crear post para comunidad '" + (current != null ? current.getName() : "") + "'", ButtonType.OK).showAndWait();
    }

    @FXML
    private void onViewPosts() {
        new Alert(Alert.AlertType.INFORMATION, "Punto de entrada: ver posts de la comunidad (pendiente)", ButtonType.OK).showAndWait();
    }

    @FXML
    private void onBack() {
        LoginApp.switchTo("/com/ug/project/ui/Communities.fxml", 1000, 600, "Comunidades - VoxPopuliDB");
    }
}
