package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;

import java.util.Date;

public class Product {
    private int id;
    private String name;
    private String category;
    private int supplierId;
    private double price;
    private int stockQuantity;
    private Date expirationDate;

    public Product(int id, String name, String category, int supplierId, double price, int stockQuantity,
            Date expirationDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.supplierId = supplierId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expirationDate = expirationDate;
    }

    public Product(String name, String category, int supplierId, double price, int stockQuantity, Date expirationDate) {
        this.name = name;
        this.category = category;
        this.supplierId = supplierId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expirationDate = expirationDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
