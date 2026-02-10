package com.handyman.oddhandyman.review.controller;

import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;
import com.handyman.oddhandyman.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling review-related operations.
 * <p>
 * Supports creating reviews after task completion, fetching reviews for a task,
 * and fetching reviews for a specific user (handyman or customer).
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Matches frontend POST /reviews
    @Operation(summary = "Submit a review after a task is completed",
            description = "Allows a customer to submit a review for a handyman after the task is marked complete",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody ReviewRequest req, Authentication auth) {
        return ResponseEntity.ok(reviewService.createReview(req, auth.getName()));
    }

    // NEW: matches frontend GET /reviews/task/{taskId}
    @Operation(summary = "Get reviews for a task",
            description = "Fetches all reviews submitted for a particular task",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ReviewResponse>> reviewsForTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(reviewService.getReviewsForTask(taskId));
    }

    // Existing: reviews for a given user
    @Operation(summary = "List reviews for a user",
            description = "Fetches all reviews associated with a specific user",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> reviewsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }
}
