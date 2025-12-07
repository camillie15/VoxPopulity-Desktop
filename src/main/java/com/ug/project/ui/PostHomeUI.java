package com.ug.project.ui;

import com.ug.project.controller.PostController;
import com.ug.project.model.Post;
import com.ug.project.service.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PostHomeUI {

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
            List<Post> posts = postcontroller.getAll();
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

        card.getChildren().addAll(title, username , content, date);

        card.setOnMouseClicked((MouseEvent e) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ug/project/ui/PostDetailAndComment.fxml"));
                Parent root = loader.load();

                PostDetailAndCommentUI controller = loader.getController();
                controller.setPost(post);

                Stage stage = (Stage) postsContainer.getScene().getWindow();
                Scene scene = new Scene(root);

                java.net.URL css = getClass().getResource("/com/ug/project/ui/style.css");
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                }

                stage.setScene(scene);
                stage.setTitle(post.getTitle());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        return card;
    }

    @FXML
    public void onCreatePost(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/CreatePost.fxml", "Crear Post");
    }

    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        chargePostsOnScreen();
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
    }
  
    @FXML
    public void viewMyPosts(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/UserPosts.fxml", "Mis posts");
    }

    @FXML
    public void onNewCommunity(ActionEvent actionEvent) {
        Navigation.switchScene(actionEvent, "/com/ug/project/ui/Communities.fxml", "Comunidades");
    }
}
