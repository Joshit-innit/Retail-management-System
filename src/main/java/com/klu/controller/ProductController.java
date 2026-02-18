package com.klu.controller;

import com.klu.model.Product;
import com.klu.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId) {
        return productService.getAllProducts()
                .stream()
                .filter(p -> p.getCategory() != null &&
                        p.getCategory().getId().equals(categoryId))
                .toList();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestParam Long categoryId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(product, categoryId));
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @PutMapping("/{id}/stock")
    public Product updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return productService.updateStock(id, quantity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
