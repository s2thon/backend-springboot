package com.example.backend.service.impl;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    @Override
    public List<Product> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Product save(Product product) {
        return repo.save(product);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Product> getByCategoryId(Long categoryId) {
        return repo.findByCategoryId(categoryId);
    }

    @Override
    public long countAllProducts() {
        return repo.count();
    }

    @Override
    public Optional<Product> findByName(String name) {
        return repo.findByName(name);
    }

    // 🆕 Seller'a ait ürünleri getir
    public List<Product> findBySellerId(Long sellerId) {
        return repo.findBySeller_Id(sellerId);
    }

    @Override
    public long countBySellerId(Long sellerId) {
        return repo.findBySeller_Id(sellerId).size();
        // Or for better performance, add a count method to the repository:
        // return repo.countBySeller_Id(sellerId);
    }

    @Override
    public long countProductsBySellerId(Long sellerId) {
        return repo.countBySellerId(sellerId);
    }

}
