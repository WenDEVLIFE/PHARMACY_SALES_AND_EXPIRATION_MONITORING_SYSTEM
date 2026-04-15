package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Supplier;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.SupplierRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.InventoryService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    @FXML
    private TextField searchField;

    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private final InventoryService inventoryService = new InventoryService();
    private final SupplierRepository supplierRepository = new SupplierRepository();
    private ObservableList<Product> productList;
    private Product selectedProduct;
    private static String initialAction;

    public static void setInitialAction(String action) {
        initialAction = action;
    }

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        expCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        setupRowFactory();
        loadProducts();
        loadSuppliers();
        handleClear();

        if ("ADD".equals(initialAction)) {
            initialAction = null;
            javafx.application.Platform.runLater(() -> nameField.requestFocus());
        } else if ("EXPIRED".equals(initialAction)) {
            initialAction = null;
            loadExpiredProducts();
        }

        setupSearch();
    }

    private void setupRowFactory() {
        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    getStyleClass().removeAll("row-critical", "row-warning");
                } else {
                    LocalDate now = LocalDate.now();
                    LocalDate exp = new java.sql.Date(item.getExpirationDate().getTime()).toLocalDate();

                    getStyleClass().removeAll("row-critical", "row-warning");
                    if (exp.isBefore(now.plusDays(7))) {
                        getStyleClass().add("row-critical");
                    } else if (exp.isBefore(now.plusDays(30))) {
                        getStyleClass().add("row-warning");
                    }
                }
            }
        });
    }

    private void loadProducts() {
        try {
            List<Product> products = inventoryService.getAllProducts();
            if (productList == null) {
                productList = FXCollections.observableArrayList(products);
            } else {
                productList.setAll(products);
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void loadExpiredProducts() {
        try {
            List<Product> products = inventoryService.getExpiringSoon(0);
            if (productList == null) {
                productList = FXCollections.observableArrayList(products);
            } else {
                productList.setAll(products);
            }
            if (filteredList != null) {
                filteredList.setPredicate(p -> true); // Reset search when switching to expired
                searchField.clear();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load expired products: " + e.getMessage());
        }
    }

    private FilteredList<Product> filteredList;

    private void setupSearch() {
        if (productList == null) return;

        filteredList = new FilteredList<>(productList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (product.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (product.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Product> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedList);
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
    private void handleTableClick() {
        selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            nameField.setText(selectedProduct.getName());
            categoryField.setText(selectedProduct.getCategory());
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            stockField.setText(String.valueOf(selectedProduct.getStockQuantity()));
            java.util.Date date = selectedProduct.getExpirationDate();
            LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
            expDatePicker.setValue(localDate);

            // Set supplier in combo
            for (Supplier s : supplierCombo.getItems()) {
                if (s.getId() == selectedProduct.getSupplierId()) {
                    supplierCombo.setValue(s);
                    break;
                }
            }
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }

    @FXML
    private void handleSave() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can add products.");
            return;
        }
        try {
            Product product = getProductFromFields();
            if (product == null)
                return;

            if (inventoryService.addProduct(product)) {
                AlertHelper.showInfo("Success", "Product added successfully.");
                loadProducts();
                handleClear();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Failed to save product: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can update products.");
            return;
        }
        if (selectedProduct == null)
            return;
        try {
            Product product = getProductFromFields();
            if (product == null)
                return;
            product.setId(selectedProduct.getId());

            if (inventoryService.updateProduct(product)) {
                AlertHelper.showInfo("Success", "Product updated successfully.");
                loadProducts();
                handleClear();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Failed to update product: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can delete products.");
            return;
        }
        if (selectedProduct == null)
            return;
        try {
            if (inventoryService.deleteProduct(selectedProduct.getId())) {
                AlertHelper.showInfo("Success", "Product deleted successfully.");
                loadProducts();
                handleClear();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to delete product: " + e.getMessage());
        }
    }

    private Product getProductFromFields() {
        try {
            String name = nameField.getText();
            String category = categoryField.getText();
            Supplier supplier = supplierCombo.getValue();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            LocalDate expDate = expDatePicker.getValue();

            if (name.isEmpty() || supplier == null || expDate == null) {
                AlertHelper.showWarning("Validation Error", "Please fill all required fields.");
                return null;
            }

            Date date = Date.from(expDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return new Product(name, category, supplier.getId(), price, stock, date);
        } catch (NumberFormatException e) {
            AlertHelper.showError("Input Error", "Please enter valid numbers for price and stock.");
            return null;
        }
    }

    @FXML
    private void handleClear() {
        selectedProduct = null;
        nameField.clear();
        categoryField.clear();
        supplierCombo.getSelectionModel().clearSelection();
        priceField.clear();
        stockField.clear();
        expDatePicker.setValue(null);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleBack() throws IOException {
        if (AuthenticationService.isAdmin()) {
            App.setRoot("admin_dashboard");
        } else {
            App.setRoot("cashier_dashboard");
        }
    }
}
