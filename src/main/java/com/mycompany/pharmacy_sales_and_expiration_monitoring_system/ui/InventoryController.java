package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Supplier;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.SupplierRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.InventoryService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class InventoryController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idCol;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, String> categoryCol;
    @FXML
    private TableColumn<Product, Double> priceCol;
    @FXML
    private TableColumn<Product, Integer> stockCol;
    @FXML
    private TableColumn<Product, Date> expCol;

    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private ComboBox<Supplier> supplierCombo;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private DatePicker expDatePicker;

    private final InventoryService inventoryService = new InventoryService();
    private final SupplierRepository supplierRepository = new SupplierRepository();
    private ObservableList<Product> productList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        expCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        loadProducts();
        loadSuppliers();
    }

    private void loadProducts() {
        try {
            productList = FXCollections.observableArrayList(inventoryService.getAllProducts());
            productTable.setItems(productList);
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.getAllSuppliers();
            supplierCombo.setItems(FXCollections.observableArrayList(suppliers));
            supplierCombo.setConverter(new StringConverter<Supplier>() {
                @Override
                public String toString(Supplier s) {
                    return s == null ? "" : s.getName();
                }

                @Override
                public Supplier fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load suppliers: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            Supplier supplier = supplierCombo.getValue();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            LocalDate expDate = expDatePicker.getValue();

            if (name.isEmpty() || supplier == null || expDate == null) {
                AlertHelper.showWarning("Validation Error", "Please fill all required fields.");
                return;
            }

            Date date = Date.from(expDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Product product = new Product(name, category, supplier.getId(), price, stock, date);

            if (inventoryService.addProduct(product)) {
                AlertHelper.showInfo("Success", "Product added successfully.");
                loadProducts();
                handleClear();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Please enter valid numbers for price and stock.");
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to save product: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        categoryField.clear();
        supplierCombo.getSelectionModel().clearSelection();
        priceField.clear();
        stockField.clear();
        expDatePicker.setValue(null);
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("admin_dashboard");
    }
}
