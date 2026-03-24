package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.DatabaseConnection;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleReturn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReturnRepository {

    public boolean processReturn(SaleReturn saleReturn) throws SQLException {
        String insertReturn = "INSERT INTO sale_returns (sale_id, product_id, quantity, reason) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

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
