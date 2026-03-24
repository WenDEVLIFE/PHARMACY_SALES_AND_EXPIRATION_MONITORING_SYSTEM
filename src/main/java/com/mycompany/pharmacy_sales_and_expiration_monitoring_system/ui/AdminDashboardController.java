package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.InventoryService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
            // Adjust this path if you moved the file into a subfolder, e.g. "/images/logo.jpg"
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
            // Optionally log e
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
    private void handleLogout() throws IOException {
        AuthenticationService.logout();
        App.setRoot("login");
    }
}