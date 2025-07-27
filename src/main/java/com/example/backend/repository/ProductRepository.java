package com.example.backend.repository;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    long count();
    Optional<Product> findByName(String name);
    List<Product> findBySeller_Id(Long sellerId);
    long countBySeller_Id(Long sellerId);

    // Satıcı ID'sine göre ürün listesi getiren metod
    List<Product> findBySellerId(Long sellerId);
    long countBySellerId(Long sellerId);
}
