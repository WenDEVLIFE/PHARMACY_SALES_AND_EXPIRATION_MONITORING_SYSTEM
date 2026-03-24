package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;

import java.util.Date;
import java.util.List;

public class Transaction {
    private int id;
    private int cashierId;
    private double totalAmount;
    private Date saleDate;
    private List<SaleItem> items;

    public Transaction(int id, int cashierId, double totalAmount, Date saleDate) {
        this.id = id;
        this.cashierId = cashierId;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public Transaction(int cashierId, double totalAmount, List<SaleItem> items) {
        this.cashierId = cashierId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
}
