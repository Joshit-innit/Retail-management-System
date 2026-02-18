package com.klu.repository;

import com.klu.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByIsActiveTrue();

    List<Product> findByNameContainingIgnoreCase(String keyword);
}
