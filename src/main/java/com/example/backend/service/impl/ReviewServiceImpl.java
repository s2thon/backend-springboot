package com.example.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.ReviewDto;
import com.example.backend.entity.Product;
import com.example.backend.entity.Review;
import com.example.backend.entity.User;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    @Override
    public Review save(ReviewDto dto) {
        Product product = productRepo.findById(dto.productId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        User user = userRepo.findById(dto.userId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        Review review = Review.builder()
            .rating(dto.rating())
            .comment(dto.comment())
            .createdAt(LocalDateTime.now())
            .product(product)
            .user(user)
            .build();

        Review savedReview = reviewRepo.save(review);

        // Ürünün ortalama puan ve yorum sayısını güncelle
        List<Review> reviews = reviewRepo.findByProductId(product.getId());
        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        product.setRate(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        product.setReviewCount(reviews.size());
        productRepo.save(product);

        return savedReview;
    }


    @Override
    public List<ReviewDto> findByProductId(Long productId) {
        return reviewRepo.findByProductId(productId).stream()
            .map(r -> new ReviewDto(
                r.getId(),
                r.getRating(),
                r.getComment(),
                r.getProduct().getId(),
                r.getUser().getId(),
                r.getCreatedAt()
            )).toList();
    }
}
