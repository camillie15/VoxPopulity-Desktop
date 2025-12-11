package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.model.Community;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CommunityRepository {
    private static final JPAUtil jpaUtil = new JPAUtil();

    public List<Community> findAll() {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            TypedQuery<Community> q = em.createQuery("SELECT c FROM Community c ORDER BY c.name", Community.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Community save(Community c) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) em.persist(c);
            else c = em.merge(c);
            em.getTransaction().commit();
            return c;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Community c) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Community attached = em.find(Community.class, c.getId());
            if (attached != null) em.remove(attached);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void join(Community c, com.ug.project.model.User u) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Community attached = em.find(Community.class, c.getId());
            com.ug.project.model.User attachedUser = em.find(com.ug.project.model.User.class, u.getId());
            if (attached != null && attachedUser != null) {
                attached.getMembers().add(attachedUser);
                attachedUser.getCommunities().add(attached);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void leave(Community c, com.ug.project.model.User u) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Community attached = em.find(Community.class, c.getId());
            com.ug.project.model.User attachedUser = em.find(com.ug.project.model.User.class, u.getId());
            if (attached != null && attachedUser != null) {
                attached.getMembers().remove(attachedUser);
                attachedUser.getCommunities().remove(attached);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    // Nuevo método para obtener una Community gestionada por el EntityManager
    public Community findById(Integer id) {
        EntityManager em = jpaUtil.getEntityManager();
        try {
            try {
                TypedQuery<Community> q = em.createQuery("SELECT c FROM Community c LEFT JOIN FETCH c.members WHERE c.id = :id", Community.class);
                q.setParameter("id", id);
                return q.getSingleResult();
            } catch (jakarta.persistence.NoResultException ex) {
                return null;
            }
        } finally {
            em.close();
        }
    }
}
