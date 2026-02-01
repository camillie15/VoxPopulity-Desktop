package com.ug.project.repository;

import com.ug.project.model.Notification;

import jakarta.persistence.EntityManager;
import java.util.List;

public class NotificationRepository {

    private final EntityManager em;

    public NotificationRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Notification n) {
        em.getTransaction().begin();
        em.persist(n);
        em.getTransaction().commit();
    }

    public List<Notification> findByUserId(Integer userId) {
        return em.createQuery(
                        "SELECT n FROM Notification n WHERE n.user.id = :uid ORDER BY n.createdAt DESC",
                        Notification.class
                )
                .setParameter("uid", userId)
                .getResultList();
    }

    public void markAsRead(Integer id) {
        em.getTransaction().begin();
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            n.setRead(true);
        }
        em.getTransaction().commit();
    }

    public void delete(Integer id) {
        em.getTransaction().begin();
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            em.remove(n);
        }
        em.getTransaction().commit();
    }

}
