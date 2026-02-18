package com.klu.dto;

import java.util.List;

public class CustomerPurchaseDto {

    private Long customerId;
    private String customerName;
    private int totalOrders;
    private double totalSpent;
    private String lastOrderDate;
    private List<String> orderNumbers;

    public CustomerPurchaseDto() {}

    // Getters & Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(String lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public List<String> getOrderNumbers() {
        return orderNumbers;
    }

    public void setOrderNumbers(List<String> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }
}
