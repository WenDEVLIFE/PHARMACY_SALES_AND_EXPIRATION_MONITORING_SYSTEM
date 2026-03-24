package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleItem;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Transaction;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    public boolean saveTransaction(Transaction transaction) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String saleQuery = "INSERT INTO sales (cashier_id, total_amount) VALUES (?, ?)";
            PreparedStatement saleStmt = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);
            saleStmt.setInt(1, transaction.getCashierId());
            saleStmt.setDouble(2, transaction.getTotalAmount());
            saleStmt.executeUpdate();

            ResultSet rs = saleStmt.getGeneratedKeys();
            if (rs.next()) {
                int saleId = rs.getInt(1);
                String itemQuery = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                String updateStockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

                PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
                PreparedStatement stockStmt = conn.prepareStatement(updateStockQuery);

                for (SaleItem item : transaction.getItems()) {
                    itemStmt.setInt(1, saleId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getUnitPrice());
                    itemStmt.addBatch();

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getProductId());
                    stockStmt.addBatch();
                }
                itemStmt.executeBatch();
                stockStmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null)
                conn.close();
        }
    }

    public List<Transaction> getDailySales(Date date) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM sales WHERE DATE(sale_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getInt("id"),
                            rs.getInt("cashier_id"),
                            rs.getDouble("total_amount"),
                            rs.getTimestamp("sale_date")));
                }
            }
        }
        return transactions;
    }
}
