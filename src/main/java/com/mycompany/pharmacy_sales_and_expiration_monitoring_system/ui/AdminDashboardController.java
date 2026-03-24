package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import javafx.fxml.FXML;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private void handleInventory() throws IOException {
        App.setRoot("inventory");
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
