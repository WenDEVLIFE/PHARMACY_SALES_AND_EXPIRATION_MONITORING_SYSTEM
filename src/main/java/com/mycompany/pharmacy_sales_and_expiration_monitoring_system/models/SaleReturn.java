package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;

import java.util.Date;

public class SaleReturn {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private String reason;
    private Date returnDate;

    public SaleReturn(int id, int saleId, int productId, int quantity, String reason, Date returnDate) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
        this.returnDate = returnDate;
    }

    public SaleReturn(int saleId, int productId, int quantity, String reason) {
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
