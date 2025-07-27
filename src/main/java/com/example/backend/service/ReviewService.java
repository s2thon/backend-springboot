package com.example.backend.service;

import java.util.List;

import com.example.backend.dto.ReviewDto;
import com.example.backend.entity.Review;

public interface ReviewService {
    Review save(ReviewDto dto);
    List<ReviewDto> findByProductId(Long productId);
}
