package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.App;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Transaction;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services.SalesService;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
    private TableColumn<Transaction, Double> subtotalCol;
    @FXML
    private TableColumn<Transaction, Double> discountCol;
    @FXML
    private TableColumn<Transaction, Double> taxCol;
    @FXML
    private TableColumn<Transaction, Double> amountCol;
    @FXML
    private TableColumn<Transaction, Date> dateCol;
    @FXML
    private TableColumn<Transaction, Void> actionCol;

    private final SalesService salesService = new SalesService();

    @FXML
    public void initialize() {
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cashierCol.setCellValueFactory(new PropertyValueFactory<>("cashierId"));
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        taxCol.setCellValueFactory(new PropertyValueFactory<>("taxAmount"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        setupActionColumn();
        loadTodaySales();
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View");
            {
                btn.setOnAction(event -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    showReceipt(t);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void showReceipt(Transaction t) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Receipt Details");
        alert.setHeaderText("Sale ID: " + t.getId());

        TextArea textArea = new TextArea(t.getReceiptText());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(300);
        textArea.setStyle("-fx-font-family: 'Monospaced';");

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
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
