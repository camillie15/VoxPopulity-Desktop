package com.ug.project.service;

import com.ug.project.model.Comment;
import com.ug.project.model.Post;
import com.ug.project.model.User;
import com.ug.project.repository.CommentRepository;
import com.ug.project.infrastructure.SessionManager;
import com.ug.project.infrastructure.JPAUtil;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

public class CommentService {

    private final JPAUtil jpaUtil = new JPAUtil();
    private final CommentRepository repo = new CommentRepository();
    private final int MAX_LENGTH = 500; // match model's length

    public boolean createComment(Integer postId, String content) {
        Integer userId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null;
        if (userId == null) {
            System.out.println("Usuario no autenticado");
            return false;
        }
        if (content == null || content.trim().isEmpty()) {
            System.out.println("Contenido vacío");
            return false;
        }
        if (content.length() > MAX_LENGTH) {
            System.out.println("Contenido demasiado largo");
            return false;
        }

        Comment c = new Comment();
        c.setContent(content.trim());

        // short-lived EM used only to obtain references
        EntityManager em = jpaUtil.getEntityManager();
        try {
            c.setUser((User) em.getReference(getUserClass(), userId));
            c.setPost((Post) em.getReference(getPostClass(), postId));
            c.setCreatedDate(LocalDateTime.now());
        } finally {
            em.close();
        }

        repo.save(c);
        return true;
    }

    public Comment updateComment(Integer commentId, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            System.out.println("Contenido vacío");
            return null;
        }
        if (newContent.length() > MAX_LENGTH) {
            System.out.println("Contenido demasiado largo");
            return null;
        }

        Comment existing = repo.findById(commentId);
        if (existing == null) {
            System.out.println("Comentario no encontrado");
            return null;
        }

        Integer currentUserId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null;
        Integer ownerId = existing.getUser() != null ? existing.getUser().getId() : null;
        if (currentUserId == null || ownerId == null || !ownerId.equals(currentUserId)) {
            System.out.println("No autorizado para editar este comentario");
            return null;
        }

        existing.setContent(newContent.trim());
        return repo.save(existing);
    }

    public List<Comment> getCommentsForPost(Integer postId) {
        return repo.findByPostId(postId);
    }

    public boolean deleteComment(Integer commentId, Long requestingUserId) {
        Comment c = repo.findById(commentId);
        if (c == null) return false;
        Integer ownerId = c.getUser() != null ? c.getUser().getId() : null;
        Integer reqId = requestingUserId == null ? null : requestingUserId.intValue();
        if (ownerId == null || !ownerId.equals(reqId)) {
            System.out.println("No autorizado para borrar este comentario");
            return false;
        }
        return repo.delete(commentId);
    }

    @SuppressWarnings("unchecked")
    private Class getUserClass() {
        try {
            return Class.forName("com.ug.project.model.User");
        } catch (ClassNotFoundException e) {
            System.out.println("User entity not found on classpath: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Class getPostClass() {
        try {
            return Class.forName("com.ug.project.model.Post");
        } catch (ClassNotFoundException e) {
            System.out.println("Post entity not found on classpath: " + e.getMessage());
            return null;
        }
    }
}