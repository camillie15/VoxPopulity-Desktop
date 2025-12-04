
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

    public boolean create (Post post){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(post);
                tx.commit();
                return true;
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

    public List<Post> findByUserId(int userId){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            try {
                String jpql = "SELECT p FROM Post p WHERE p.status = 1 AND p.user.id = :userId ORDER BY p.id DESC";
                TypedQuery<Post> query = em.createQuery(jpql, Post.class);
                query.setParameter("userId", userId);
                return query.getResultList();
            } catch (Exception e) {
                System.out.println("Error PostRepository - findByUserId: " + e);
                throw new RuntimeException(e);
            }
        }
    }

    public boolean update(Post post) {
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Post existing = em.find(Post.class, post.getId());
                existing.setTitle(post.getTitle());
                existing.setContent(post.getContent());
                em.merge(existing);
                tx.commit();
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al actualizar", e);
            }
        }
    }

    public boolean delete(int idPost){
        try (EntityManager em = jpaUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Post existing = em.find(Post.class, idPost);
                existing.setStatus(0);
                tx.commit();
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw new RuntimeException("Error al eliminar", e);
            }
        }
    }
}
