package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.User;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class ForgotPasswordController {

    @FXML
    private VBox step1Box;

    @FXML
    private VBox step2Box;

    @FXML
    private TextField usernameField;

    @FXML
    private Label questionLabel;

    @FXML
    private TextField answerField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label statusLabel;

    private final UserRepository userRepository = new UserRepository();
    private User targetUser;

    @FXML
    private void handleCheckUsername() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username.");
            return;
        }

        try {
            targetUser = userRepository.getUserByUsername(username);
            if (targetUser != null) {
                if (targetUser.getSecurityQuestion() != null && !targetUser.getSecurityQuestion().isEmpty()) {
                    questionLabel.setText(targetUser.getSecurityQuestion());
                    step1Box.setVisible(false);
                    step1Box.setManaged(false);
                    step2Box.setVisible(true);
                    step2Box.setManaged(true);
                    statusLabel.setText("");
                } else {
                    statusLabel.setText("No security question set for this user. Contact Admin.");
                }
            } else {
                statusLabel.setText("Username not found.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetPassword() {
        String answer = answerField.getText();
        String newPassword = newPasswordField.getText();

        if (answer.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (answer.equalsIgnoreCase(targetUser.getSecurityAnswer())) {
            try {
                if (userRepository.updatePassword(targetUser.getId(), newPassword)) {
                    statusLabel.setText("Password reset successful! You can now login.");
                    statusLabel.setStyle("-fx-text-fill: #27ae60;"); // Green color
                    step2Box.setDisable(true);
                } else {
                    statusLabel.setText("Failed to update password.");
                }
            } catch (SQLException e) {
                statusLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Incorrect security answer.");
        }
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        App.setRoot("login");
    }
}
