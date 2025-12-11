
package com.ug.project;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.repository.TestConnection;
import com.ug.project.ui.App;

public class ProjectValidation {


    public static void main(String[] args) {

        App app = new App();
        try {

            JPAUtil jpa = new JPAUtil();
            TestConnection testConnection = new TestConnection();
            testConnection.testConnection();
            app.runApp(args);

        } catch (Throwable ex) {
            System.err.println("Advertencia: no se pudo inicializar JPA antes de arrancar JavaFX: " + ex);
        }


    }
}
