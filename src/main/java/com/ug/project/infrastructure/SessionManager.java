package com.ug.project.infrastructure;

import com.ug.project.model.User;

public final class SessionManager {
    private static User currentUser;

    private SessionManager() {}

    public static void setCurrentUser(User u) { currentUser = u; }
    public static User getCurrentUser() { return currentUser; }
    public static boolean isAuthenticated() { return currentUser != null; }
    public static void clear() { currentUser = null; }
}

