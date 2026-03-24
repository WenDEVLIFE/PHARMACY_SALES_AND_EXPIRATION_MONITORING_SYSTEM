package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    public List<Product> getExpiringProducts(int daysThreshold) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE expiration_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, daysThreshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    public boolean addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, category, supplier_id, price, stock_quantity, expiration_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setInt(3, product.getSupplierId());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setDate(6, new java.sql.Date(product.getExpirationDate().getTime()));
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStock(int productId, int newQuantity) throws SQLException {
        String query = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("supplier_id"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                rs.getDate("expiration_date"));
    }
}
