package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;

import java.util.Date;
import java.util.List;

public class Transaction {
    private int id;
    private int cashierId;
    private double subtotal;
    private double discountAmount;
    private double taxAmount;
    private double totalAmount;
    private String discountType;
    private String receiptText;
    private Date saleDate;
    private List<SaleItem> items;

    public Transaction(int id, int cashierId, double totalAmount, Date saleDate) {
        this.id = id;
        this.cashierId = cashierId;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public Transaction(int id, int cashierId, double subtotal, double discountAmount, double taxAmount,
            double totalAmount, String discountType, String receiptText, Date saleDate) {
        this.id = id;
        this.cashierId = cashierId;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.discountType = discountType;
        this.receiptText = receiptText;
        this.saleDate = saleDate;
    }

    public Transaction(int cashierId, double subtotal, double discountAmount, double taxAmount, double totalAmount,
            String discountType, String receiptText, List<SaleItem> items) {
        this.cashierId = cashierId;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.discountType = discountType;
        this.receiptText = receiptText;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getReceiptText() {
        return receiptText;
    }

    public void setReceiptText(String receiptText) {
        this.receiptText = receiptText;
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
