
package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

public class PostRepository {

    private final JPAUtil jpaUtil = new JPAUtil();

    public void create (Post post){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(post);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al crear el Post", e);
            }
        }
    }

    public List<Post> findAll() {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                String jpql = "SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.id DESC";
                TypedQuery<Post> query = em.createQuery(jpql, Post.class);
                return query.getResultList();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<Post> findById(Integer id) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                Post post = em.find(Post.class, id);
                return Optional.ofNullable(post);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update(Post post) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.merge(post);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al actualizar", e);
            }
        }
    }
}
