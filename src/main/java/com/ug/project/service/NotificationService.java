package com.ug.project.service;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.model.Notification;
import com.ug.project.model.User;
import com.ug.project.repository.NotificationRepository;

import jakarta.persistence.EntityManager;
import com.ug.project.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService() {
        EntityManager em = JPAUtil.getEntityManager();
        this.repo = new NotificationRepository(em);
    }

    /**
     * Envía una notificación a un usuario
     */
    public void send(User user, String message) {
        Notification n = new Notification();
        n.setUser(user);
        n.setMessage(message);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        repo.save(n);
    }

    /**
     * Lista todas las notificaciones de un usuario
     */
    public List<Notification> list(Integer userId) {
        return repo.findByUserId(userId);
    }

    /**
     * Marca una notificación como leída
     */
    public void markAsRead(Integer id) {
        repo.markAsRead(id);
    }

    public void markAllRead(Integer userId) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            List<Notification> list = em.createQuery(
                            "SELECT n FROM Notification n WHERE n.user.id = :uid", Notification.class)
                    .setParameter("uid", userId)
                    .getResultList();

            for (Notification n : list) {
                n.setRead(true);
                em.merge(n);
            }

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    public void notifyLogin(User user) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Notification n = new Notification();
            n.setUser(user);
            n.setMessage("Has iniciado sesión. ¡Bienvenido a VoxPopuly!");
            n.setRead(false);
            n.setCreatedAt(LocalDateTime.now());

            em.persist(n);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    public void notifyNewPost(User user, Post post) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Notification n = new Notification();
            n.setUser(user);
            n.setMessage("Publicaste \"" + post.getTitle() + "\" exitosamente.");
            n.setRead(false);
            n.setCreatedAt(LocalDateTime.now());

            em.persist(n);

            em.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        repo.delete(id);
    }

}
