package com.klu.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.klu.dto.CustomerPurchaseDto;
import com.klu.dto.SalesReportDto;
import com.klu.dto.TopProductDto;
import com.klu.model.Customer;
import com.klu.model.Order;
import com.klu.model.OrderItem;
import com.klu.model.Product;
import com.klu.repository.CustomerRepository;
import com.klu.repository.OrderItemRepository;
import com.klu.repository.OrderRepository;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public ReportService(OrderRepository orderRepository,
                         OrderItemRepository orderItemRepository,
                         CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
    }

    // ===============================
    // 1️⃣ SALES SUMMARY (Date Range)
    // ===============================
    public SalesReportDto getSalesReport(LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        List<Order> orders = orderRepository
                .findByOrderDateBetween(start, end);

        int totalOrders = orders.size();
        int totalItemsSold = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Order order : orders) {
            totalRevenue = totalRevenue.add(order.getTotalAmount());

            for (OrderItem item : order.getOrderItems()) {
                totalItemsSold += item.getQuantity();
            }
        }

        double averageOrderValue = totalOrders == 0 ? 0 :
                totalRevenue.doubleValue() / totalOrders;

        SalesReportDto dto = new SalesReportDto();
        dto.setFromDate(from.toString());
        dto.setToDate(to.toString());
        dto.setTotalOrders(totalOrders);
        dto.setTotalItemsSold(totalItemsSold);
        dto.setTotalRevenue(totalRevenue.doubleValue());
        dto.setAverageOrderValue(averageOrderValue);

        return dto;
    }

    // ===============================
    // 2️⃣ TOP SELLING PRODUCTS
    // ===============================
    public List<TopProductDto> getTopProducts(int limit) {

        List<OrderItem> items = orderItemRepository.findAll();

        Map<Product, Integer> quantityMap = new HashMap<>();
        Map<Product, BigDecimal> revenueMap = new HashMap<>();

        for (OrderItem item : items) {

            Product product = item.getProduct();

            quantityMap.put(product,
                    quantityMap.getOrDefault(product, 0)
                            + item.getQuantity());

            revenueMap.put(product,
                    revenueMap.getOrDefault(product, BigDecimal.ZERO)
                            .add(item.getLineTotal()));
        }

        return quantityMap.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .map(entry -> {

                    Product p = entry.getKey();

                    TopProductDto dto = new TopProductDto();
                    dto.setProductId(p.getId());
                    dto.setProductCode(p.getProductCode());
                    dto.setProductName(p.getName());
                    dto.setCategoryName(
                            p.getCategory() != null ?
                                    p.getCategory().getName() : "N/A");
                    dto.setTotalQuantitySold(entry.getValue());
                    dto.setTotalRevenue(
                            revenueMap.get(p).doubleValue());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ====================================
    // 3️⃣ CUSTOMER PURCHASE HISTORY
    // ====================================
    public CustomerPurchaseDto getCustomerPurchaseHistory(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        List<Order> orders =
                orderRepository.findByCustomerId(customerId);

        double totalSpent = 0;
        List<String> orderNumbers = new ArrayList<>();
        LocalDateTime lastOrderDate = null;

        for (Order order : orders) {
            totalSpent += order.getTotalAmount().doubleValue();
            orderNumbers.add(order.getOrderNumber());

            if (lastOrderDate == null ||
                    order.getOrderDate().isAfter(lastOrderDate)) {
                lastOrderDate = order.getOrderDate();
            }
        }

        CustomerPurchaseDto dto = new CustomerPurchaseDto();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(
                customer.getFirstName() + " " + customer.getLastName());
        dto.setTotalOrders(orders.size());
        dto.setTotalSpent(totalSpent);
        dto.setLastOrderDate(
                lastOrderDate != null ? lastOrderDate.toString() : null);
        dto.setOrderNumbers(orderNumbers);

        return dto;
    }

    // ====================================
    // 4️⃣ REVENUE BY CATEGORY
    // ====================================
    public Map<String, Double> getRevenueByCategory() {

        List<OrderItem> items = orderItemRepository.findAll();
        Map<String, Double> categoryRevenue = new HashMap<>();

        for (OrderItem item : items) {

            String categoryName = item.getProduct()
                    .getCategory().getName();

            categoryRevenue.put(
                    categoryName,
                    categoryRevenue.getOrDefault(categoryName, 0.0)
                            + item.getLineTotal().doubleValue()
            );
        }

        return categoryRevenue;
    }

    // ====================================
    // 5️⃣ DAILY SALES
    // ====================================
    public double getDailySales(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Order> orders =
                orderRepository.findByOrderDateBetween(start, end);

        double total = 0;

        for (Order order : orders) {
            total += order.getTotalAmount().doubleValue();
        }

        return total;
    }
}
