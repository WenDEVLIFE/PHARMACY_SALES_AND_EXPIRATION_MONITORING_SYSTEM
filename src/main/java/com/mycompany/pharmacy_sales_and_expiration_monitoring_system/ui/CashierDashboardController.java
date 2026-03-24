package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  // <-- add this import

import java.io.IOException;

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