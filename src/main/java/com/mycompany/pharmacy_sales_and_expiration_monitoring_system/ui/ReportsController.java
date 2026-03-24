package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Transaction;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.SalesService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class ReportsController {

    @FXML
    private TableView<Transaction> reportsTable;
    @FXML
    private TableColumn<Transaction, Integer> saleIdCol;
    @FXML
    private TableColumn<Transaction, Integer> cashierCol;
    @FXML
    private TableColumn<Transaction, Date> dateCol;
    @FXML
    private TableColumn<Transaction, Double> amountCol;

    private final SalesService salesService = new SalesService();

    @FXML
    public void initialize() {
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cashierCol.setCellValueFactory(new PropertyValueFactory<>("cashierId"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        loadTodaySales();
    }

    private void loadTodaySales() {
        try {
            reportsTable.setItems(FXCollections.observableArrayList(salesService.getDailyReport(new Date())));
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load reports: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("admin_dashboard");
    }
}
