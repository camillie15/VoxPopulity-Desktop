package com.ug.project.ui;

import com.ug.project.controller.PostController;
import com.ug.project.model.Community;
import com.ug.project.service.CommunityService;
import com.ug.project.service.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Optional;

public class CreatePostUI {

    private final PostController postController = new PostController();
    private final CommunityService communityService = new CommunityService();

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private ComboBox<CommunityItem> comunidadComboBox;

    private final ObservableList<CommunityItem> comunidades = FXCollections.observableArrayList();

    // Si se solicita seleccionar una comunidad antes de initialize, la guardamos aquí
    private Integer pendingSelectedCommunityId = null;

    @FXML
    public void initialize() {
        // Inicializar las comunidades
        getCommunities();
        comunidadComboBox.setPromptText("(Obligatorio) Selecciona una comunidad");
        // Aplicar selección pendiente si existe
        if (pendingSelectedCommunityId != null) {
            for (CommunityItem it : comunidades) {
                if (it.getId() == pendingSelectedCommunityId) {
                    comunidadComboBox.getSelectionModel().select(it);
                    break;
                }
            }
            pendingSelectedCommunityId = null;
        }
    }

    private void getCommunities() {
        // Cargar comunidades desde la BD
        comunidades.clear();
        for (Community c : communityService.listAll()) {
            if (c != null && c.getId() != null) {
                comunidades.add(new CommunityItem(c.getId(), c.getName()));
            }
        }

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

        //Si no se seleccionó comunidad, enviar null para que el servicio lo interprete
        Integer comunidadId = (comunidadSeleccionada != null) ? comunidadSeleccionada.getId() : null;

        if (titulo.trim().isEmpty()) {
            warningScreen();
            return;
        }

        if (contenido.trim().isEmpty()) {
            warningScreen();
            return;
        }

        // Validación: comunidad obligatoria
        if (comunidadId == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Debes seleccionar una comunidad antes de crear la publicación.");
            a.setTitle("Comunidad obligatoria");
            a.setHeaderText(null);
            a.showAndWait();
            return;
        }

        boolean ok = postController.savePost(titulo, contenido, comunidadId);
        if (ok) {
            ButtonType volver = new ButtonType("Volver al muro", ButtonBar.ButtonData.OK_DONE);
            ButtonType crearOtra = new ButtonType("Crear otra", ButtonBar.ButtonData.OTHER);
            Alert a = new Alert(Alert.AlertType.INFORMATION, "La publicación se creó con éxito.", volver, crearOtra);
            a.setTitle("Publicación creada");
            a.setHeaderText(null);
            Optional<ButtonType> res = a.showAndWait();
            if (res.isPresent() && res.get() == volver) {
                Navigation.switchScene(actionEvent, "/com/ug/project/ui/Dashboard.fxml", "Dashboard");
            } else {
                // limpiar campos para crear otra
                titleField.clear();
                contentArea.clear();
                // mantener la selección de comunidad
                titleField.requestFocus();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "No se pudo crear la publicación. Intenta nuevamente.");
            a.setTitle("Error");
            a.setHeaderText(null);
            a.showAndWait();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/Dashboard.fxml", "Dashboard");
    }

    // Permite a otros controladores preseleccionar una comunidad al abrir esta vista
    public void setSelectedCommunityId(Integer communityId) {
        if (communityId == null) return;
        // Si ya hay items, seleccionar inmediatamente
        if (!comunidades.isEmpty()) {
            for (CommunityItem it : comunidades) {
                if (it.getId() == communityId) {
                    comunidadComboBox.getSelectionModel().select(it);
                    return;
                }
            }
        }
        // Si aún no se inicializó, guardar la selección pendiente
        pendingSelectedCommunityId = communityId;
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