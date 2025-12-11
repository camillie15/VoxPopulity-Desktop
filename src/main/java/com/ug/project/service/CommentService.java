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
    private final CommentRepository repo;
    private final int MAX_LENGTH = 500; // match model's length

    public CommentService(CommentRepository repo) {
        this.repo = repo;
    }

    public Comment createComment(Integer postId, String content) {
        Integer userId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null;
        if (userId == null) {
            System.out.println("Usuario no autenticado");
            return null;
        }
        if (content == null || content.trim().isEmpty()) {
            System.out.println("Contenido vacío");
            return null;
        }
        if (content.length() > MAX_LENGTH) {
            System.out.println("Contenido demasiado largo");
            return null;
        }

        Comment c = new Comment();
        c.setContent(content.trim());

        EntityManager em = jpaUtil.getEntityManager();

        Post postRef = null;
        User userRef = null;

        try {
            userRef = (User) em.getReference(getUserClass(), userId);
            postRef = (Post) em.getReference(getPostClass(), postId);

            c.setUser(userRef);
            c.setPost(postRef);
            c.setCreatedDate(LocalDateTime.now());

        } finally {
            em.close();
        }

        // 1️⃣ Guardar comentario
        Comment saved = repo.save(c);

        // 2️⃣ Notificar al dueño del post (si no es la misma persona)
        try {
            if (postRef != null && postRef.getUser() != null) {
                Integer ownerId = postRef.getUser().getId();

                if (!ownerId.equals(userId)) {
                    NotificationService ns = new NotificationService();
                    String msg = userRef.getUsername()
                            + " comentó tu publicación: \""
                            + postRef.getTitle() + "\"";

                    ns.send(postRef.getUser(), msg);
                    System.out.println("🔔 Notificación enviada al dueño del post.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al notificar comentario: " + e);
        }

        return saved;
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