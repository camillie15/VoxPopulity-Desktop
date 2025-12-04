package com.ug.project.ui;

import com.ug.project.controller.PostController;
import com.ug.project.service.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreatePostUI {

    private final PostController postController = new PostController();

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private ComboBox<CommunityItem> comunidadComboBox;

    private final ObservableList<CommunityItem> comunidades = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializar las comunidades
        getCommunities();
    }

    private void getCommunities(){
        //Agregar comunidades desde la BD
        comunidades.add(new CommunityItem(1, "Todos"));
        comunidades.add(new CommunityItem(2, "Comunidad 1 - no funka"));
        comunidades.add(new CommunityItem(3, "Comunidad 2 - no funka"));
        comunidades.add(new CommunityItem(4, "Comunidad 3 - no funka"));

        comunidadComboBox.setItems(comunidades);
    }

    private void warningScreen() {
        Navigation.openNewScreen("/com/ug/project/ui/Warning.fxml", "Campos vacios");
    }


    public void onCreatePost(ActionEvent actionEvent) {

        //Obtener todos los datos del fxml
        String titulo = titleField.getText();
        String contenido = contentArea.getText();
        CommunityItem comunidadSeleccionada = comunidadComboBox.getValue();

        //Si ocurre algo mal, id comunidad siempre será 1
        int comunidadId = (comunidadSeleccionada != null) ? comunidadSeleccionada.getId() : 1;

        if (titulo.trim().isEmpty()) {
            warningScreen();
            return;
        }

        if (contenido.trim().isEmpty()) {
            warningScreen();
            return;
        }
        postController.savePost(titulo, contenido, comunidadId);
    }

    public void onCancel(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/Dashboard.fxml", "Dashboard");
    }
}

class CommunityItem {
    int id;
    String name;

    @Override
    public String toString() {
        return name;
    }

    public CommunityItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}