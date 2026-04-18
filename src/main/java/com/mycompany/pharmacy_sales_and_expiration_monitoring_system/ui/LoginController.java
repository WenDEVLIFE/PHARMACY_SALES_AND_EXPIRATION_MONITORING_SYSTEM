// Java
package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private javafx.scene.control.Hyperlink forgotPasswordLink;

    @FXML
    private ImageView logoImageView;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        try {
            String path = "/images/loggy.png"; // matches target/classes/images/logo.jpg
            var resource = getClass().getResource(path);
            if (resource == null) {
                System.err.println("LOGO ERROR: Resource not found at " + path);
                return;
            }
            if (logoImageView == null) {
                System.err.println("LOGO ERROR: logoImageView is null (check fx:id in FXML)");
                return;
            }

            // load synchronously so width/height are known immediately
            Image logo = new Image(resource.toExternalForm(), false);
            System.out.println("LOGO size: " + logo.getWidth() + "x" + logo.getHeight());

            logoImageView.setImage(logo);
            logoImageView.setPreserveRatio(true);
            logoImageView.setSmooth(true);
            logoImageView.setCache(true);
        } catch (Exception e) {
            System.err.println("LOGO ERROR: Exception loading logo: " + e.getMessage());
            e.printStackTrace();
        }
    }


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
                forgotPasswordLink.setVisible(true);
            }
        } catch (SQLException | IOException e) {
            statusLabel.setText("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            App.setRoot("forgot_password");
        } catch (IOException e) {
            statusLabel.setText("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}