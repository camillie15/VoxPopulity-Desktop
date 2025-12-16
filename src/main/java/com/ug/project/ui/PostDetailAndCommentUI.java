package com.ug.project.ui;

import com.ug.project.controller.CommentController;
import com.ug.project.model.Comment;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.infrastructure.SessionManager;
import com.ug.project.service.Navigation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PostDetailAndCommentUI {

    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label dateLabel;
    @FXML private Label contentLabel;
    @FXML private VBox commentsContainer;
    @FXML private TextArea newCommentArea;
    @FXML private Button addCommentButton;
    @FXML private Button backButton;

    private final CommentController commentController = new CommentController();
    private Post post;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public void setPost(Post post) {
        this.post = post;
        commentController.setPostId(post != null ? post.getId() : null);
        drawPost();
        loadComments();
    }

    @FXML
    private void initialize() {
        addCommentButton.setOnAction(a -> onAddComment());
        backButton.setOnAction(a -> Navigation.switchScene(a, "/com/ug/project/ui/Dashboard.fxml", "Dashboard"));
    }

    private void drawPost() {
        if (post == null) return;
        titleLabel.setText(post.getTitle());
        User u = post.getUser();
        usernameLabel.setText(u != null ? u.getUsername() : "unknown");
        dateLabel.setText("Publicado: " + post.getCreatedDate().format(formatter));
        contentLabel.setText(post.getContent());
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        List<Comment> comments = commentController.getCommentsForPost(post.getId());
        Integer currentUserId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null;

        for (Comment c : comments) {
            VBox commentCard = new VBox(4);
            commentCard.setStyle("-fx-padding: 6; -fx-background-color: #fff;");

            HBox header = new HBox(8);
            Label commentUser = new Label(c.getUser() != null ? c.getUser().getUsername() : "unknown");
            commentUser.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");

            Label commentDate = new Label(c.getCreatedDate() != null ? c.getCreatedDate().format(formatter) : "");
            commentDate.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
            Region headerSpacer = new Region();
            HBox.setHgrow(headerSpacer, Priority.ALWAYS);
            header.getChildren().addAll(commentUser, headerSpacer, commentDate);

            Label content = new Label(c.getContent());
            content.setWrapText(true);
            content.setMaxWidth(Double.MAX_VALUE);

            HBox actionsRow = new HBox(8);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Integer ownerId = c.getUser() != null ? c.getUser().getId() : null;
            if (currentUserId != null && ownerId != null && currentUserId.equals(ownerId)) {
                Button editBtn = new Button("Edit");
                Button delBtn = new Button("Delete");

                editBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10;");
                delBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10;");

                editBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar edición");
                    confirm.setHeaderText("Editar comentario");
                    confirm.setContentText("¿Deseas editar este comentario?");
                    Optional<ButtonType> res = confirm.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.OK) {
                        showEditInline(commentCard, c);
                    }
                });

                delBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar eliminación");
                    confirm.setHeaderText("Eliminar comentario");
                    confirm.setContentText("¿Estás seguro que deseas eliminar este comentario?");
                    Optional<ButtonType> res = confirm.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.OK) {
                        boolean ok = commentController.deleteComment(c.getId(), currentUserId != null ? currentUserId.longValue() : null);
                        if (ok) loadComments();
                        else showError("No se pudo eliminar el comentario.");
                    }
                });

                actionsRow.getChildren().addAll(spacer, editBtn, delBtn);
            } else {
                actionsRow.getChildren().add(spacer);
            }

            commentCard.getChildren().addAll(header, content, actionsRow);
            commentsContainer.getChildren().add(commentCard);
        }
    }

    private void showEditInline(VBox commentCard, Comment comment) {
        commentCard.getChildren().clear();
        TextArea editArea = new TextArea(comment.getContent());
        editArea.setPrefRowCount(2);
        Button save = new Button("Save");
        Button cancel = new Button("Cancel");

        save.setOnAction(s -> {
            boolean updated = commentController.updateComment(comment.getId(), editArea.getText());
            if (updated) loadComments();
            else showError("No se pudo actualizar el comentario.");
        });
        cancel.setOnAction(c -> loadComments());

        HBox actions = new HBox(8);
        actions.getChildren().addAll(save, cancel);

        commentCard.getChildren().addAll(editArea, actions);
    }

    private void onAddComment() {
        String text = newCommentArea.getText();
        try {
            boolean created = commentController.createComment(post.getId(), text);
            if (created) {
                newCommentArea.clear();
                loadComments();
                showInfo("Comentario creado");
            } else {
                showError("No se pudo crear el comentario.");
            }
        } catch (Exception e) {
            showError("No se pudo crear el comentario.");
        }
    }

    private void showInfo(String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Información");
        info.setHeaderText(null);
        info.setContentText(message);
        info.showAndWait();
    }

    private void showError(String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(null);
        error.setContentText(message);
        error.showAndWait();
    }
}