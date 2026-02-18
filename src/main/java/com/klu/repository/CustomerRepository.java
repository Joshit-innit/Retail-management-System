package com.klu.repository;

import com.klu.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByCustomerCode(String customerCode);

    List<Customer> findByIsActiveTrue();
}
