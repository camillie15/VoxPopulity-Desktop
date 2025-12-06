package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import jakarta.persistence.EntityManager;

public class TestConnection {

    public boolean testConnection() {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();  // Usamos el singleton

            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();

            System.out.println("Connection Successfully");
            return true;

        } catch (Exception ex) {
            System.err.println("Error trying connection to DB: " + ex);
            return false;

        } finally {
            if (em != null && em.isOpen()) {
                em.close();  // ✔ SOLO cerramos el EntityManager
            }
        }
    }
}
