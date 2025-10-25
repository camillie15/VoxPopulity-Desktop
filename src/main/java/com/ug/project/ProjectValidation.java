
package com.ug.project;

import com.ug.project.infrastructure.JPAUtil;
import com.ug.project.repository.TestConnection;

public class ProjectValidation {

    public static void main(String[] args) {

        JPAUtil jpa = new JPAUtil();
        TestConnection testConnection = new TestConnection(jpa);

        testConnection.testConnection();
    }
}
