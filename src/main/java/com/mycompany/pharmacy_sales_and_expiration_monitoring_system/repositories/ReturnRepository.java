package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.DatabaseConnection;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleReturn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnRepository {

    /**
     * Returns the total quantity already returned for a specific product in a sale.
     */
    public int getAlreadyReturnedQty(int saleId, int productId) throws SQLException {
        String query = "SELECT COALESCE(SUM(quantity), 0) FROM sale_returns WHERE sale_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Returns the original purchased quantity for a product in a sale.
     */
    public int getOriginalQty(int saleId, int productId) throws SQLException {
        String query = "SELECT quantity FROM sale_items WHERE sale_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        return 0;
    }

    public boolean processReturn(SaleReturn saleReturn) throws SQLException {
        String insertReturn = "INSERT INTO sale_returns (sale_id, product_id, quantity, reason) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // --- Guard: check remaining returnable quantity ---
            int originalQty = getOriginalQty(saleReturn.getSaleId(), saleReturn.getProductId());
            int alreadyReturned = getAlreadyReturnedQty(saleReturn.getSaleId(), saleReturn.getProductId());
            int remainingReturnable = originalQty - alreadyReturned;

            if (saleReturn.getQuantity() > remainingReturnable) {
                throw new SQLException(
                    "Cannot return " + saleReturn.getQuantity() + " unit(s). " +
                    "Only " + remainingReturnable + " unit(s) can still be returned for this item."
                );
            }

            // 1. Insert return record
            try (PreparedStatement stmt = conn.prepareStatement(insertReturn)) {
                stmt.setInt(1, saleReturn.getSaleId());
                stmt.setInt(2, saleReturn.getProductId());
                stmt.setInt(3, saleReturn.getQuantity());
                stmt.setString(4, saleReturn.getReason());
                stmt.executeUpdate();
            }

            // 2. Update stock
            try (PreparedStatement stmt = conn.prepareStatement(updateStock)) {
                stmt.setInt(1, saleReturn.getQuantity());
                stmt.setInt(2, saleReturn.getProductId());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null)
                conn.setAutoCommit(true);
        }
    }
}
