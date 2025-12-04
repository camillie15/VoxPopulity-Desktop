package com.ug.project.repository;

import com.ug.project.model.Comment;
import com.ug.project.infrastructure.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CommentRepository {

    private final  JPAUtil jpaUtil = new JPAUtil();
    public Comment save(Comment comment) {
        EntityManager entityManager = jpaUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            if (comment.getId() == null) {
                entityManager.persist(comment);
            } else {
                comment = entityManager.merge(comment);
            }
            entityManager.getTransaction().commit();
            return comment;
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    public List<Comment> findByPostId(Integer postId) {
        EntityManager entityManager = jpaUtil.getEntityManager();
        try {
            TypedQuery<Comment> q = entityManager.createQuery(
                    "SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId ORDER BY c.createdDate DESC",
                    Comment.class);
            q.setParameter("postId", postId);
            return q.getResultList();
        } finally {
            entityManager.close();
        }
    }

    public Comment findById(Integer id) {
        EntityManager entityManager = jpaUtil.getEntityManager();
        try {
            return entityManager.find(Comment.class, id);
        } finally {
            entityManager.close();
        }
    }

    public boolean delete(Integer id) {
        EntityManager entityManager = jpaUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Comment c = entityManager.find(Comment.class, id);
            if (c == null) {
                entityManager.getTransaction().commit();
                return false;
            }
            entityManager.remove(c);
            entityManager.getTransaction().commit();
            return true;
        } catch (RuntimeException ex) {
            if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}