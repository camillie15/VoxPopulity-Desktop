package com.ug.project.ui;

import com.ug.project.controller.CommentController;
import com.ug.project.infrastructure.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * UI class: handles JavaFX controls and user events. Delegates business logic to
 * com.ug.project.controller.CommentController (in controller package).
 */
public class CommentUI implements Initializable {

    @FXML private Label lblPostTitle;
    @FXML private Label lblPostContent;
    @FXML private Label lblUser;
    @FXML private ListView<String> lvComments;
    @FXML private TextArea txtNewComment;
    @FXML private Button btnAdd;

    private CommentController controller;

    public void setController(CommentController controller) {
        this.controller = controller;
        refreshComments();
    }

    /**
     * Called by main screen to set which post to show.
     */
    public void setPost(Integer postId, String title, String content) {
        lblPostTitle.setText(title == null ? "" : title);
        lblPostContent.setText(content == null ? "" : content);
        if (controller != null) controller.setPostId(postId);
        refreshComments();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUserLabel();
    }

    private void updateUserLabel() {
        Integer userId = SessionManager.getCurrentUser().getId();
        lblUser.setText(userId == null ? "(no user logged)" : "userId: " + userId);
    }

    @FXML
    private void onAdd() {
        if (controller == null) {
            showError("Controller not set");
            return;
        }
        String text = txtNewComment.getText();
        try {
            controller.addComment(text);
            txtNewComment.clear();
            refreshComments();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showWarning(ex.getMessage());
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
        }
    }

    private void refreshComments() {
        if (controller == null) return;
        try {
            List<String> items = controller.getFormattedComments();
            lvComments.getItems().setAll(items);
        } catch (Exception ignored) {}
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }
}