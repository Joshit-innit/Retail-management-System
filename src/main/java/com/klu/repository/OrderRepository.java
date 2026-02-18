package com.klu.repository;

import com.klu.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByStatus(String status);

    List<Order> findByOrderDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    Optional<Order> findByOrderNumber(String orderNumber);
}
