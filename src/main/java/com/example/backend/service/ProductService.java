package com.example.backend.service;

import com.example.backend.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    List<Product> getByCategoryId(Long categoryId);
    long countAllProducts();
    Optional<Product> findByName(String name);
    List<Product> findBySellerId(Long sellerId);
    long countBySellerId(Long sellerId);
    long countProductsBySellerId(Long sellerId);
}
