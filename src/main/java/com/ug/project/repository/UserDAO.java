// src/main/java/com/ug/project/repository/UserDAO.java
package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.model.User;
import jakarta.persistence.*;

public class UserDAO {

    private final JPAUtil jpaUtil = new JPAUtil();

    public User login(String username, String password) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :u AND u.password = :p", User.class);
            q.setParameter("u", username);
            q.setParameter("p", password);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean register(User u) {
        EntityManager em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            // verifica duplicados por restricciones únicas de tu tabla
            Long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :un OR u.email = :em", Long.class)
                    .setParameter("un", u.getUsername())
                    .setParameter("em", u.getEmail())
                    .getSingleResult();
            if (count != 0) {
                throw new IllegalStateException("Usuario o email ya registrado.");
            }

            tx.begin();
            em.persist(u);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            // Si MySQL tira excepción por UNIQUE, la devolvemos como mensaje amigable
            if (ex instanceof PersistenceException) {
                throw new IllegalStateException("Usuario o email ya registrado.");
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
