package com.ug.project.ui;

import com.ug.project.model.Comment;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.CommentRepository;
import com.ug.project.service.CommentService;
import com.ug.project.infrastructure.SessionManager;
import com.ug.project.service.Navigation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostDetailAndCommentUI {

    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label dateLabel;
    @FXML private Label contentLabel;
    @FXML private VBox commentsContainer;
    @FXML private TextArea newCommentArea;
    @FXML private Button addCommentButton;
    @FXML private Button backButton;

    private final CommentService commentService = new CommentService(new CommentRepository());
    private Post post;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public void setPost(Post post) {
        this.post = post;
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
        // show username (not email)
        usernameLabel.setText(u != null ? u.getUsername() : "unknown");
        dateLabel.setText("Publicado: " + post.getCreatedDate().format(formatter));
        contentLabel.setText(post.getContent());
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        List<Comment> comments = commentService.getCommentsForPost(post.getId());
        Integer currentUserId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null;

        for (Comment c : comments) {
            // Vertical card per comment: header (user + date) + content + optional buttons row
            VBox commentCard = new VBox(4);
            commentCard.setStyle("-fx-padding: 6; -fx-background-color: #fff;");

            // Header: username left, date right
            HBox header = new HBox(8);
            Label commentUser = new Label(c.getUser() != null ? c.getUser().getUsername() : "unknown");
            commentUser.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");

            Label commentDate = new Label(c.getCreatedDate() != null ? c.getCreatedDate().format(formatter) : "");
            commentDate.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
            Region headerSpacer = new Region();
            HBox.setHgrow(headerSpacer, Priority.ALWAYS);
            header.getChildren().addAll(commentUser, headerSpacer, commentDate);

            // Content
            Label content = new Label(c.getContent());
            content.setWrapText(true);
            content.setMaxWidth(Double.MAX_VALUE);

            // Buttons row (edit/delete) aligned to the right if owner
            HBox actionsRow = new HBox(8);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Integer ownerId = c.getUser() != null ? c.getUser().getId() : null;
            if (currentUserId != null && ownerId != null && currentUserId.equals(ownerId)) {
                Button editBtn = new Button("Edit");
                Button delBtn = new Button("Delete");

                // keep previous coloring (no bold)
                editBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10;");
                delBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 4 10 4 10;");

                editBtn.setOnAction(e -> showEditInline(commentCard, c));
                delBtn.setOnAction(e -> {
                    boolean ok = commentService.deleteComment(c.getId(), currentUserId.longValue());
                    if (ok) loadComments();
                    else System.out.println("Could not delete comment");
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
            Comment updated = commentService.updateComment(comment.getId(), editArea.getText());
            if (updated != null) loadComments();
            else System.out.println("Could not update comment");
        });
        cancel.setOnAction(c -> loadComments());

        HBox actions = new HBox(8);
        actions.getChildren().addAll(save, cancel);

        commentCard.getChildren().addAll(editArea, actions);
    }

    private void onAddComment() {
        String text = newCommentArea.getText();
        Comment created = commentService.createComment(post.getId(), text);
        if (created != null) {
            newCommentArea.clear();
            loadComments();
        } else {
            System.out.println("Could not create comment");
        }
    }
}