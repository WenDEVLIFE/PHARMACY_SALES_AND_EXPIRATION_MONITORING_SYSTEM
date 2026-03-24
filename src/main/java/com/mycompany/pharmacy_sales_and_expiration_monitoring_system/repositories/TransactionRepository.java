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

            String saleQuery = "INSERT INTO sales (cashier_id, subtotal, discount_amount, tax_amount, total_amount, discount_type, receipt_text) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement saleStmt = conn.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);
            saleStmt.setInt(1, transaction.getCashierId());
            saleStmt.setDouble(2, transaction.getSubtotal());
            saleStmt.setDouble(3, transaction.getDiscountAmount());
            saleStmt.setDouble(4, transaction.getTaxAmount());
            saleStmt.setDouble(5, transaction.getTotalAmount());
            saleStmt.setString(6, transaction.getDiscountType());
            saleStmt.setString(7, transaction.getReceiptText());
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
                            rs.getDouble("subtotal"),
                            rs.getDouble("discount_amount"),
                            rs.getDouble("tax_amount"),
                            rs.getDouble("total_amount"),
                            rs.getString("discount_type"),
                            rs.getString("receipt_text"),
                            rs.getTimestamp("sale_date")));
                }
            }
        }
        return transactions;
    }

    public List<SaleItem> getSaleItems(int saleId) throws SQLException {
        List<SaleItem> items = new ArrayList<>();
        String query = "SELECT si.*, p.name as product_name FROM sale_items si JOIN products p ON si.product_id = p.id WHERE si.sale_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem(
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"));
                    item.setProductName(rs.getString("product_name"));
                    item.setSaleId(saleId);
                    items.add(item);
                }
            }
        }
        return items;
    }

    public Transaction getTransactionById(int saleId) throws SQLException {
        String query = "SELECT * FROM sales WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Transaction(
                            rs.getInt("id"),
                            rs.getInt("cashier_id"),
                            rs.getDouble("subtotal"),
                            rs.getDouble("discount_amount"),
                            rs.getDouble("tax_amount"),
                            rs.getDouble("total_amount"),
                            rs.getString("discount_type"),
                            rs.getString("receipt_text"),
                            rs.getTimestamp("sale_date"));
                }
            }
        }
        return null;
    }

    public List<Object[]> getMonthlyTopSelling(String month) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT p.name, SUM(si.quantity) as total_qty " +
                "FROM sale_items si " +
                "JOIN sales s ON si.sale_id = s.id " +
                "JOIN products p ON si.product_id = p.id " +
                "WHERE DATE_FORMAT(s.sale_date, '%Y-%m') = ? " +
                "GROUP BY si.product_id " +
                "ORDER BY total_qty DESC " +
                "LIMIT 5";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[] { rs.getString("name"), rs.getInt("total_qty") });
                }
            }
        }
        return results;
    }
}
