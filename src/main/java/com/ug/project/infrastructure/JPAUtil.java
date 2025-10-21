
package com.ug.project.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    
    private static final String PERSISTENCE_UNIT_NAME = "db-connection";
    private static EntityManagerFactory factory;
    
    public JPAUtil(){    
        try{
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }catch(Throwable ex) {
            System.err.println("La creación del EntityManagerFactory falló." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public EntityManager getEntityManager(){
        return factory.createEntityManager();
    }
    
    public void close(){
        if(factory != null && factory.isOpen()){
            factory.close();
        }
    }
    
}
