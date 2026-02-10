package com.handyman.oddhandyman.review.service;

import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;

import java.util.List;

/**
 * Service interface for managing {@link Review} entities.
 * <p>
 * Declares operations for creating reviews, retrieving reviews by user or task,
 * and converting reviews to response DTOs.
 */
public interface ReviewService {

    /**
     * Creates a new review for a task by a specific user.
     *
     * @param req the {@link ReviewRequest} containing rating, comment, and task ID
     * @param userEmail email of the user submitting the review
     * @return the created {@link Review} entity
     */
    Review createReview(ReviewRequest req, String userEmail);

    /**
     * Retrieves all reviews written about a specific user (handyman).
     *
     * @param userId ID of the handyman
     * @return a list of {@link Review} entities for the specified user
     */
    List<Review> getReviewsForUser(Long userId);

    /**
     * Retrieves all reviews associated with a specific task.
     *
     * @param taskId ID of the task
     * @return a list of {@link ReviewResponse} DTOs for the task
     */
    List<ReviewResponse> getReviewsForTask(Long taskId);
}
