package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleItem;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Transaction;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.TransactionRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesService {
    private final TransactionRepository transactionRepository;
    private List<SaleItem> currentCart;

    public SalesService() {
        this.transactionRepository = new TransactionRepository();
        this.currentCart = new ArrayList<>();
    }

    public void addToCart(int productId, int quantity, double unitPrice) {
        currentCart.add(new SaleItem(productId, quantity, unitPrice));
    }

    public void clearCart() {
        currentCart.clear();
    }

    public double getSubtotal() {
        return currentCart.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    }

    public boolean completeSale(int cashierId, double subtotal, double discountAmount, double taxAmount,
            double totalAmount, String discountType, String receiptText) throws SQLException {
        if (currentCart.isEmpty())
            return false;

        Transaction transaction = new Transaction(cashierId, subtotal, discountAmount, taxAmount, totalAmount,
                discountType, receiptText, new ArrayList<>(currentCart));
        boolean success = transactionRepository.saveTransaction(transaction);
        if (success) {
            clearCart();
        }
        return success;
    }

    public List<Transaction> getDailyReport(java.util.Date date) throws SQLException {
        return transactionRepository.getDailySales(new java.sql.Date(date.getTime()));
    }
}
