package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            if (authService.login(username, password)) {
                String role = AuthenticationService.getCurrentUser().getRole();
                if ("ADMIN".equals(role)) {
                    App.setRoot("admin_dashboard");
                } else {
                    App.setRoot("cashier_dashboard");
                }
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            statusLabel.setText("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
