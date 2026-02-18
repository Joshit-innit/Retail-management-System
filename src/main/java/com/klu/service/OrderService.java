package com.klu.service;

import com.klu.model.*;
import com.klu.repository.*;
import com.klu.dto.OrderRequestDto;
import com.klu.dto.OrderItemRequestDto;
import com.klu.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public Order createOrder(OrderRequestDto request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));

        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMode(request.getPaymentMode());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : request.getItems()) {

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found"));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(product.getUnitPrice());

            BigDecimal lineTotal = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            item.setLineTotal(lineTotal);

            order.addOrderItem(item);

            // Reduce stock
            product.setStockQuantity(
                    product.getStockQuantity() - itemDto.getQuantity());

            productRepository.save(product);

            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
