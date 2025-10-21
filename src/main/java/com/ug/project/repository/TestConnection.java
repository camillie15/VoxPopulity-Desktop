
package com.ug.project.repository;

import com.ug.project.infrastructure.JPAUtil;
import jakarta.persistence.EntityManager;

public class TestConnection {
    
    EntityManager em = null;
    
    JPAUtil jpa;

    public TestConnection(JPAUtil jpa) {
        this.jpa = jpa;
    }
    
    public boolean testConnection(){
        
        try{
            em = jpa.getEntityManager();
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.getTransaction().commit();
            System.out.println("Connection Sucessfully");
        } catch (Exception ex){
            System.err.println("Error tring connection to DB" + ex);
            return false;
        } finally{
            if(em !=  null && em.isOpen())
                jpa.close();
        }
        
        return false;
    }
    
    
    
}
