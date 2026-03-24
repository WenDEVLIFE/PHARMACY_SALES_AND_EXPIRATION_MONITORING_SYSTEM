package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Supplier;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.SupplierRepository;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.AuthenticationService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;

public class SuppliersController {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, Integer> idCol;
    @FXML
    private TableColumn<Supplier, String> nameCol;
    @FXML
    private TableColumn<Supplier, String> contactCol;
    @FXML
    private TableColumn<Supplier, String> phoneCol;
    @FXML
    private TableColumn<Supplier, String> addressCol;

    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;

    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private final SupplierRepository supplierRepository = new SupplierRepository();
    private Supplier selectedSupplier;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadSuppliers();
        handleClear();
    }

    private void loadSuppliers() {
        try {
            supplierTable.setItems(FXCollections.observableArrayList(supplierRepository.getAllSuppliers()));
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to load suppliers: " + e.getMessage());
        }
    }

    @FXML
    private void handleTableClick() {
        selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            nameField.setText(selectedSupplier.getName());
            contactField.setText(selectedSupplier.getContactPerson());
            phoneField.setText(selectedSupplier.getPhone());
            addressField.setText(selectedSupplier.getAddress());
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }

    @FXML
    private void handleSave() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can add suppliers.");
            return;
        }
        Supplier supplier = getSupplierFromFields();
        if (supplier == null)
            return;

        try {
            if (supplierRepository.addSupplier(supplier)) {
                AlertHelper.showInfo("Success", "Supplier added successfully.");
                loadSuppliers();
                handleClear();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to save supplier: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can update suppliers.");
            return;
        }
        if (selectedSupplier == null)
            return;
        Supplier supplier = getSupplierFromFields();
        if (supplier == null)
            return;
        supplier.setId(selectedSupplier.getId());

        try {
            if (supplierRepository.updateSupplier(supplier)) {
                AlertHelper.showInfo("Success", "Supplier updated successfully.");
                loadSuppliers();
                handleClear();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to update supplier: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (!AuthenticationService.isAdmin()) {
            AlertHelper.showError("Access Denied", "Only administrators can delete suppliers.");
            return;
        }
        if (selectedSupplier == null)
            return;
        try {
            if (supplierRepository.deleteSupplier(selectedSupplier.getId())) {
                AlertHelper.showInfo("Success", "Supplier deleted successfully.");
                loadSuppliers();
                handleClear();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Failed to delete supplier: " + e.getMessage());
        }
    }

    private Supplier getSupplierFromFields() {
        String name = nameField.getText();
        String contact = contactField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (name.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Supplier name is required.");
            return null;
        }
        return new Supplier(name, contact, phone, address);
    }

    @FXML
    private void handleClear() {
        selectedSupplier = null;
        nameField.clear();
        contactField.clear();
        phoneField.clear();
        addressField.clear();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("admin_dashboard");
    }
}
