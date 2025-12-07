package com.ug.project.controller;

import com.ug.project.model.Community;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.service.CommunityService;
import com.ug.project.service.PostService;
import com.ug.project.ui.LoginApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommunityDetailsController {

    @FXML private Label lblName;
    @FXML private Label lblDescription;
    @FXML private Label lblMembers;
    @FXML private ListView<Post> lvPosts;
    @FXML private ListView<User> lvMembers;

    private Community current;
    private final PostService postService = new PostService();
    private final CommunityService communityService = new CommunityService();

    public void setCommunity(Community c) {
        this.current = c;
        refresh();
    }

    private void refresh() {
        if (current == null) return;
        lblName.setText(current.getName());
        lblDescription.setText(current.getDescription() == null ? "" : current.getDescription());
        lblMembers.setText("Miembros: " + current.getMembersCount());

        // Cargar posts de la comunidad
        try {
            List<Post> posts = postService.getAllByCommunityId(current.getId());
            lvPosts.getItems().setAll(posts);
        } catch (Exception ex) {
            // ignore for now
        }

        // Cargar miembros desde la entidad (ya viene con members inicializados porque usamos JOIN FETCH)
        lvMembers.getItems().setAll(current.getMembers());

        // Cell factories para mostrar información compacta
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        lvPosts.setCellFactory(lv -> new ListCell<>(){
            @Override protected void updateItem(Post p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); setGraphic(null); }
                else {
                    String user = p.getUser() != null ? p.getUser().getUsername() : "";
                    String date = p.getCreatedDate() != null ? p.getCreatedDate().format(fmt) : "";
                    setText(p.getTitle() + "\nby " + user + " · " + date + "\n" + (p.getContent() == null ? "" : p.getContent()));
                }
            }
        });

        lvMembers.setCellFactory(lv -> new ListCell<>(){
            @Override protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) { setText(null); setGraphic(null); }
                else { setText(u.getName() + " (" + u.getUsername() + ")"); }
            }
        });
    }

    @FXML
    private void onCreatePost() {
        if (current == null) return;
        // Abrir CreatePost.fxml y pasar la comunidad seleccionada mediante initializer
        LoginApp.switchToWithInit("/com/ug/project/ui/CreatePost.fxml", 800, 600,
                "Crear Post - " + current.getName(), (com.ug.project.ui.CreatePostUI ctrl) -> {
                    try {
                        ctrl.getClass().getMethod("setSelectedCommunityId", Integer.class).invoke(ctrl, current.getId());
                    } catch (Exception ex) {
                        // si falla, abrir igual y el usuario podrá seleccionar manualmente
                    }
                });
    }

    @FXML
    private void onViewPosts() {
        // ya mostramos posts en la lista, esta acción puede centrar/filtrar si se desea
    }

    @FXML
    private void onBack() {
        LoginApp.switchTo("/com/ug/project/ui/Communities.fxml", 1000, 600, "Comunidades - VoxPopuliDB");
    }
}
