package com.ug.project.controller;

import com.ug.project.model.Comment;
import com.ug.project.service.CommentService;
import com.ug.project.infrastructure.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller logic (business operations). Keeps current post id and uses CommentService.
 */
public class CommentController {

    private final CommentService service;
    private Integer currentPostId;

    public CommentController(CommentService service) {
        this.service = service;
    }

    public void setPostId(Integer postId) {
        this.currentPostId = postId;
    }

    public List<String> getFormattedComments() {
        if (currentPostId == null) return Collections.emptyList();
        List<Comment> comments = service.getCommentsForPost(currentPostId);
        return comments.stream()
                .map(this::formatComment)
                .collect(Collectors.toList());
    }

    public boolean addComment(String content) {
        Integer userId = SessionManager.getCurrentUser().getId();
        if (userId == null) throw new IllegalStateException("Debe iniciar sesión");
        if (currentPostId == null) throw new IllegalStateException("No post selected");
        return service.createComment(currentPostId, content);
    }

    private String formatComment(Comment c) {
        String userPart = (c.getUser() != null) ? "user:" + c.getUser().getId() : "user:?";
        return "[" + c.getCreatedDate() + "] " + userPart + " - " + c.getContent();
    }
}