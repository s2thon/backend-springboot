package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.ReviewDto;
import com.example.backend.entity.Review;
import com.example.backend.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Yeni bir review oluşturmak:
     * POST /api/products/{productId}/reviews
     */
    @PostMapping
public ResponseEntity<ReviewDto> create(
        @PathVariable Long productId,
        @RequestBody ReviewDto dto) {

    // Record immutable, bu yüzden yeni bir ReviewDto oluşturuyoruz:
    ReviewDto toSave = new ReviewDto(
        null,               // id (yeni kayıt, henüz yok)
        dto.rating(),       // body’den gelen rating
        dto.comment(),      // body’den gelen comment
        productId,          // path’ten gelen productId
        dto.userId(),       // body’den gelen userId
        null                // createdAt (servis atayacak)
    );

    Review review = reviewService.save(toSave);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toDto(review));
}


    /**
     * Bir ürünün tüm review’lerini listelemek:
     * GET /api/products/{productId}/reviews
     */
    @GetMapping
    public ResponseEntity<List<ReviewDto>> listByProduct(
            @PathVariable Long productId) {

        List<ReviewDto> reviews = reviewService.findByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // -------------------------
    // Entity → DTO dönüşümü
    // -------------------------
    private ReviewDto toDto(Review r) {
        return new ReviewDto(
            r.getId(),
            r.getRating(),
            r.getComment(),
            r.getProduct().getId(),
            r.getUser().getId(),
            r.getCreatedAt()
        );
    }
}
