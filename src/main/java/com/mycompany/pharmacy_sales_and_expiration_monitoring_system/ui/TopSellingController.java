package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.TransactionRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TopSellingController {

    @FXML
    private TextField monthField;
    @FXML
    private VBox resultsBox;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<TopSellingItem> topSellingTable;
    @FXML
    private TableColumn<TopSellingItem, Integer> rankCol;
    @FXML
    private TableColumn<TopSellingItem, String> productNameCol;
    @FXML
    private TableColumn<TopSellingItem, Integer> qtySoldCol;

    private final TransactionRepository transactionRepo = new TransactionRepository();

    @FXML
    public void initialize() {
        rankCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRank()));
        productNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        qtySoldCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQty()));

        // Set current month as default
        monthField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    @FXML
    private void handleAnalyze() {
        String month = monthField.getText().trim();
        if (!month.matches("\\d{4}-\\d{2}")) {
            AlertHelper.showError("Error", "Invalid format. Please use YYYY-MM.");
            return;
        }

        try {
            List<Object[]> data = transactionRepo.getMonthlyTopSelling(month);
            if (data.isEmpty()) {
                AlertHelper.showInfo("Information", "No sales found for this month.");
                resultsBox.setVisible(false);
                return;
            }

            List<TopSellingItem> items = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                Object[] row = data.get(i);
                items.add(new TopSellingItem(i + 1, (String) row[0], (Integer) row[1]));
            }

            topSellingTable.setItems(FXCollections.observableArrayList(items));
            titleLabel.setText("TOP 5 SELLING PRODUCTS (" + month + ")");
            resultsBox.setVisible(true);

        } catch (SQLException e) {
            AlertHelper.showError("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("admin_dashboard");
    }

    // Helper inner class for TableView
    public static class TopSellingItem {
        private final int rank;
        private final String name;
        private final int qty;

        public TopSellingItem(int rank, String name, int qty) {
            this.rank = rank;
            this.name = name;
            this.qty = qty;
        }

        public int getRank() {
            return rank;
        }

        public String getName() {
            return name;
        }

        public int getQty() {
            return qty;
        }
    }
}
