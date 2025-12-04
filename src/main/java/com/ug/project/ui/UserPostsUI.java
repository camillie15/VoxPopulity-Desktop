package com.ug.project.ui;

import com.ug.project.controller.PostController;
import com.ug.project.infrastructure.PostContext;
import com.ug.project.model.Post;
import com.ug.project.service.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserPostsUI {

    @FXML
    private VBox postsContainer;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    private final PostController postcontroller = new PostController();

    @FXML
    public void initialize(){
        chargePostsOnScreen();
    }

    private void chargePostsOnScreen () {
        try {
            List<Post> posts = postcontroller.getPostsCurrentUser();
            drawOnUI(posts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void drawOnUI (List<Post> posts) {
        postsContainer.getChildren().clear();

        for (var post: posts){
            VBox postCard = createPostCard(post);
            postsContainer.getChildren().add(postCard);
        }
    }

    private VBox createPostCard(Post post) {
        VBox card = new VBox(8);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10;");

        Label title = new Label(post.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label username = new Label(post.getUser().getUsername());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label content = new Label(post.getContent());
        content.setWrapText(true);

        String formatDate = post.getCreatedDate().format(formatter);
        Label date = new Label("Publicado: " + formatDate);
        date.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        // Botón de eliminar
        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleDeletePost(post.getId(),event));

        // Botón de editar
        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: #44dd44; -fx-text-fill: white;");
        editButton.setOnAction(event -> handleEditPost(post));

        // Contenedor para los botones (por si quieres agregar más después)
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(deleteButton, editButton);

        card.getChildren().addAll(title, username , content, date, buttonContainer);
        return card;
    }

    public void handleDeletePost(int idPost, ActionEvent actionEvent){
        System.out.println("Eliminando post con id: "+ idPost);
        boolean response = postcontroller.delete(idPost);
        if (response) {
            Navigation.switchScene(actionEvent, "/com/ug/project/ui/Dashboard.fxml", "Dashboard");
        } else {
            Navigation.openNewScreen("/com/ug/project/ui/Warning.fxml", "Error al eliminar");
        }
    }

    public void handleEditPost(Post post){
        System.out.println("Actualizando post con id: "+ post.toString());
        System.out.println(post.toString());
        PostContext.getInstance().setCurrentPost(post);
        Navigation.openNewScreen("/com/ug/project/ui/EditPost.fxml", "Editar Post");

    }

    public void onBack(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/Dashboard.fxml", "Dashboard");
    }
}
