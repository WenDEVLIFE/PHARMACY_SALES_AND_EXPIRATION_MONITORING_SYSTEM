package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.User;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.UserRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.InventoryService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    private final InventoryService inventoryService = new InventoryService();

    @FXML
    private ImageView logoImageView;

    @FXML
    public void initialize() {
        loadLogo();
        checkExpirations();
    }

    private void loadLogo() {
        try {
            String path = "/images/loggy.png";
            var resource = getClass().getResource(path);
            if (resource != null && logoImageView != null) {
                Image logo = new Image(resource.toExternalForm(), true);
                logoImageView.setImage(logo);
            }
        } catch (Exception e) {
            System.err.println("Failed to load logo: " + e.getMessage());
        }
    }

    private void checkExpirations() {
        try {
            List<Product> expiringSoon = inventoryService.getExpiringSoon(30);
            if (!expiringSoon.isEmpty()) {
                long criticalCount = expiringSoon.stream()
                        .filter(p -> {
                            LocalDate exp = new java.sql.Date(p.getExpirationDate().getTime()).toLocalDate();
                            return exp.isBefore(LocalDate.now().plusDays(7));
                        }).count();

                String message = String.format(
                        "You have %d products expiring within 30 days.\n- %d are critical (within 7 days or expired).",
                        expiringSoon.size(), criticalCount);

                AlertHelper.showWarning("Expiration Alert", message);
            }
        } catch (SQLException e) {
            System.err.println("Error checking expirations: " + e.getMessage());
        }
    }

    @FXML
    private void handleInventory() throws IOException {
        App.setRoot("inventory");
    }

    @FXML
    private void handleSuppliers() throws IOException {
        App.setRoot("suppliers");
    }

    @FXML
    private void handleUsers() throws IOException {
        App.setRoot("users");
    }

    @FXML
    private void handleSales() throws IOException {
        App.setRoot("sales");
    }

    @FXML
    private void handleReports() throws IOException {
        App.setRoot("reports");
    }

    @FXML
    private void handleMonthlyTop5Selling() throws IOException {
        App.setRoot("top_selling");
    }

    @FXML
    private void handleReturnItem() throws IOException {
        App.setRoot("return_item");
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

    @FXML
    private void handleLogout() throws IOException {
        AuthenticationService.logout();
        App.setRoot("login");
    }
}