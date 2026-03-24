package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleItem;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.InventoryService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.SalesService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalesController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productSearchTable;
    @FXML
    private TableColumn<Product, String> searchNameCol;
    @FXML
    private TableColumn<Product, Double> searchPriceCol;
    @FXML
    private TableColumn<Product, Integer> searchStockCol;
    @FXML
    private TableColumn<Product, Void> searchActionCol;

    @FXML
    private TableView<SaleItem> cartTable;
    @FXML
    private TableColumn<SaleItem, String> cartNameCol;
    @FXML
    private TableColumn<SaleItem, Integer> cartQtyCol;
    @FXML
    private TableColumn<SaleItem, Double> cartPriceCol;
    @FXML
    private TableColumn<SaleItem, Double> cartSubtotalCol;

    @FXML
    private Label totalLabel;

    private final InventoryService inventoryService = new InventoryService();
    private final SalesService salesService = new SalesService();
    private List<Product> allProducts = new ArrayList<>();
    private ObservableList<SaleItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupSearchTable();
        setupCartTable();
        loadAllProducts();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));
    }

    private void setupSearchTable() {
        searchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        searchStockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        searchActionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Add");
            {
                btn.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    addToCart(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void setupCartTable() {
        cartNameCol.setCellValueFactory(cellData -> {
            int productId = cellData.getValue().getProductId();
            String name = allProducts.stream()
                    .filter(p -> p.getId() == productId)
                    .map(Product::getName)
                    .findFirst().orElse("Unknown");
            return new SimpleStringProperty(name);
        });
        cartQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cartSubtotalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getQuantity() * cellData.getValue().getUnitPrice()).asObject());
        cartTable.setItems(cartItems);
    }

    private void loadAllProducts() {
        try {
            allProducts = inventoryService.getAllProducts();
            productSearchTable.setItems(FXCollections.observableArrayList(allProducts));
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void filterProducts(String keyword) {
        List<Product> filtered = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        productSearchTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addToCart(Product p) {
        if (p.getStockQuantity() <= 0) {
            AlertHelper.showWarning("Out of Stock", "This product is currently unavailable.");
            return;
        }
        if (inventoryService.isExpired(p)) {
            AlertHelper.showWarning("Expired Product", "Cannot sell expired products!");
            return;
        }

        SaleItem existing = cartItems.stream()
                .filter(item -> item.getProductId() == p.getId())
                .findFirst().orElse(null);

        if (existing != null) {
            if (existing.getQuantity() < p.getStockQuantity()) {
                existing.setQuantity(existing.getQuantity() + 1);
                cartTable.refresh();
            } else {
                AlertHelper.showWarning("Stock Limit", "No more stock available.");
            }
        } else {
            cartItems.add(new SaleItem(p.getId(), 1, p.getPrice()));
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        totalLabel.setText(String.format("%.2f", total));
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty())
            return;

        try {
            int cashierId = AuthenticationService.getCurrentUser().getId();
            salesService.clearCart();
            for (SaleItem item : cartItems) {
                salesService.addToCart(item.getProductId(), item.getQuantity(), item.getUnitPrice());
            }

            if (salesService.completeSale(cashierId)) {
                AlertHelper.showInfo("Transaction Complete", "Sale recorded successfully.");
                cartItems.clear();
                updateTotal();
                loadAllProducts(); // Refresh stock
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to complete transaction: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearCart() {
        cartItems.clear();
        updateTotal();
    }

    @FXML
    private void handleBack() throws IOException {
        String role = AuthenticationService.getCurrentUser().getRole();
        if ("ADMIN".equals(role))
            App.setRoot("admin_dashboard");
        else
            App.setRoot("cashier_dashboard");
    }
}
