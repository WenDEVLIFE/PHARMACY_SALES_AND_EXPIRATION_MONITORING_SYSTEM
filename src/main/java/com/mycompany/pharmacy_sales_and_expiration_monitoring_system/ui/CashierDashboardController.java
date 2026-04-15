package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.User;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.UserRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Optional;

public class CashierDashboardController {

    @FXML
    private ImageView logoImageView;

    public void initialize() {
        loadLogo();
    }

    @FXML
    private void handleSales() throws IOException {
        App.setRoot("sales");
    }

    @FXML
    private void handleLogout() throws IOException {
        AuthenticationService.logout();
        App.setRoot("login");
    }

    @FXML
    private void handleViewStock() throws IOException {
        App.setRoot("inventory");
    }

    @FXML
    private void handleCheckExpired() throws IOException {
        InventoryController.setInitialAction("EXPIRED");
        App.setRoot("inventory");
    }

    @FXML
    private void handlePasswordChange() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change Your Password");
        dialog.setContentText("Please enter new password:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            if (password.trim().isEmpty()) {
                AlertHelper.showError("Error", "Password cannot be empty.");
                return;
            }
            try {
                User user = AuthenticationService.getCurrentUser();
                UserRepository userRepo = new UserRepository();
                if (userRepo.updatePassword(user.getId(), password)) {
                    AlertHelper.showInfo("Success", "Password updated successfully.");
                    user.setPassword(password);
                } else {
                    AlertHelper.showError("Error", "Failed to update password.");
                }
            } catch (Exception e) {
                AlertHelper.showError("Error", "Database error: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void loadLogo() {
        try {
            String path = "/images/loggy.png";
            var resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println("Logo resource not found at: " + path);
                return;
            }

            if (logoImageView == null) {
                System.err.println("logoImageView is null. Check fx:id in FXML.");
                return;
            }

            Image logo = new Image(resource.toExternalForm(), true);
            if (logo.isError()) {
                System.err.println("Error loading logo image: " + logo.getException());
            } else {
                logoImageView.setImage(logo);
                System.out.println("Logo loaded from: " + resource);
            }
        } catch (Exception e) {
            System.err.println("Failed to load logo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}