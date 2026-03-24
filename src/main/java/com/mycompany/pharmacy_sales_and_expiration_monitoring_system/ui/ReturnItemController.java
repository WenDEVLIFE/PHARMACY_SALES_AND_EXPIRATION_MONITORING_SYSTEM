package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleItem;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Transaction;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.TransactionRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.ReturnService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReturnItemController {

    @FXML
    private TextField saleIdField;
    @FXML
    private VBox saleDetailsBox;
    @FXML
    private Label saleInfoLabel;
    @FXML
    private TableView<SaleItem> itemsTable;
    @FXML
    private TableColumn<SaleItem, String> productNameCol;
    @FXML
    private TableColumn<SaleItem, Integer> quantityCol;
    @FXML
    private TableColumn<SaleItem, Double> priceCol;
    @FXML
    private TextField returnQtyField;
    @FXML
    private TextField reasonField;

    private final TransactionRepository transactionRepo = new TransactionRepository();
    private final ReturnService returnService = new ReturnService();

    @FXML
    public void initialize() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    @FXML
    private void handleSearch() {
        String saleIdText = saleIdField.getText().trim();
        if (saleIdText.isEmpty()) {
            AlertHelper.showError("Error", "Please enter a Sale ID.");
            return;
        }

        try {
            int saleId = Integer.parseInt(saleIdText);
            Transaction transaction = transactionRepo.getTransactionById(saleId);

            if (transaction == null) {
                AlertHelper.showError("Error", "Sale ID not found.");
                saleDetailsBox.setVisible(false);
                return;
            }

            List<SaleItem> items = transactionRepo.getSaleItems(saleId);
            itemsTable.setItems(FXCollections.observableArrayList(items));

            saleInfoLabel.setText("Sale ID: " + saleId + " | Date: " + transaction.getSaleDate() + " | Total: ₱"
                    + transaction.getTotalAmount());
            saleDetailsBox.setVisible(true);

        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Invalid Sale ID format.");
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleProcessReturn() {
        SaleItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertHelper.showError("Error", "Please select an item to return.");
            return;
        }

        String qtyText = returnQtyField.getText().trim();
        String reason = reasonField.getText().trim();

        if (qtyText.isEmpty() || reason.isEmpty()) {
            AlertHelper.showError("Error", "Return quantity and reason are required.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0 || qty > selectedItem.getQuantity()) {
                AlertHelper.showError("Error", "Invalid return quantity. Max allowed: " + selectedItem.getQuantity());
                return;
            }

            if (returnService.processReturn(selectedItem.getSaleId(), selectedItem.getProductId(), qty, reason)) {
                AlertHelper.showInfo("Success", "Return processed successfully. Stock updated.");
                handleSearch(); // Refresh
                returnQtyField.clear();
                reasonField.clear();
            } else {
                AlertHelper.showError("Error", "Failed to process return.");
            }

        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Invalid quantity format.");
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("admin_dashboard");
    }
}
