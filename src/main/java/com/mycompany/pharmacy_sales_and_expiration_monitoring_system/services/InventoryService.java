package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.Product;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.ProductRepository;

import java.sql.SQLException;
import java.util.List;

public class InventoryService {
    private final ProductRepository productRepository;

    public InventoryService() {
        this.productRepository = new ProductRepository();
    }

    public List<Product> getAllProducts() throws SQLException {
        return productRepository.getAllProducts();
    }

    public List<Product> getExpiringSoon(int days) throws SQLException {
        return productRepository.getExpiringProducts(days);
    }

    public boolean isExpired(Product product) {
        return product.getExpirationDate().before(new java.util.Date());
    }

    public boolean addProduct(Product product) throws SQLException {
        return productRepository.addProduct(product);
    }

    public boolean updateProduct(Product product) throws SQLException {
        return productRepository.updateProduct(product);
    }

    public boolean deleteProduct(int id) throws SQLException {
        return productRepository.deleteProduct(id);
    }
}
