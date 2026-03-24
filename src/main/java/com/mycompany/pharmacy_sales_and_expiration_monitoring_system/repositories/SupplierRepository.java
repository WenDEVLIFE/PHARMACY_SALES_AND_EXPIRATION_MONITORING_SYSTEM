package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Supplier;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepository {
    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("address")));
            }
        }
        return suppliers;
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {
        String query = "INSERT INTO suppliers (name, contact_person, phone, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getAddress());
            return stmt.executeUpdate() > 0;
        }
    }
}
