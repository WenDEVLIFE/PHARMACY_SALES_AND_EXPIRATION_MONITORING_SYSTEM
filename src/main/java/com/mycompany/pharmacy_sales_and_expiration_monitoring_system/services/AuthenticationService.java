package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.User;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.UserRepository;

import java.sql.SQLException;

public class AuthenticationService {
    private final UserRepository userRepository;
    private static User currentUser;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public boolean login(String username, String password) throws SQLException {
        User user = userRepository.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }
}
