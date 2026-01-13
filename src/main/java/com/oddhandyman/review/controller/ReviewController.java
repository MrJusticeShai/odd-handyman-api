package com.oddhandyman.review.controller;

import com.oddhandyman.review.dto.ReviewRequest;
import com.oddhandyman.review.dto.ReviewResponse;
import com.oddhandyman.review.entity.Review;
import com.oddhandyman.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Matches frontend POST /reviews
    @Operation(summary = "Submit a review after a task is completed", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody ReviewRequest req, Authentication auth) {
        return ResponseEntity.ok(reviewService.createReview(req, auth.getName()));
    }

    // NEW: matches frontend GET /reviews/task/{taskId}
    @Operation(summary = "Get reviews for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ReviewResponse>> reviewsForTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(reviewService.getReviewsForTask(taskId));
    }

    // Existing: reviews for a given user
    @Operation(summary = "List reviews for a user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> reviewsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }
}
