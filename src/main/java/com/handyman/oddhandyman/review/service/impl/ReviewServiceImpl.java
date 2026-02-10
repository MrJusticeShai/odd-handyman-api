package com.handyman.oddhandyman.review.service.impl;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.exception.TaskNotFoundException;
import com.handyman.oddhandyman.exception.UnacceptableOperationException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;
import com.handyman.oddhandyman.review.repository.ReviewRepository;
import com.handyman.oddhandyman.review.service.ReviewService;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link ReviewService} for managing reviews of completed tasks.
 * <p>
 * Handles creating reviews, fetching reviews for tasks or handymen, and converting
 * {@link Review} entities to {@link ReviewResponse} DTOs.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public ReviewServiceImpl(ReviewRepository reviewRepo, TaskRepository taskRepo, UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    /**
     * Creates a new review for a completed task by the customer who requested the task.
     *
     * @param req       the {@link ReviewRequest} containing rating, comment, and task ID
     * @param userEmail email of the customer submitting the review
     * @return the created {@link Review} entity
     * @throws UserNotFoundException            if the customer email is not found
     * @throws TaskNotFoundException            if the task does not exist
     * @throws UnacceptableOperationException  if the task is not completed, the task has already been reviewed,
     *                                         or the reviewer is not the task's customer
     */
    public Review createReview(ReviewRequest req, String userEmail) {

        User reviewer = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        Task task = taskRepo.findById(req.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));

        if (!task.getCustomer().getId().equals(reviewer.getId())) {
            throw new UnacceptableOperationException("Customer Cannot Review Someone Else's Task!");
        }

        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new UnacceptableOperationException("Cannot Leave Review Until Task Is Completed.");
        }

        if (reviewRepo.existsByTaskId(task.getId())) {
            throw new UnacceptableOperationException("This Task Already Has A Review.");
        }

        Review review = new Review();
        review.setTask(task);
        review.setReviewer(reviewer);
        review.setReviewedHandyman(task.getAssignedHandyman());
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        return reviewRepo.save(review);
    }

    /**
     * Retrieves all reviews written about a specific handyman.
     *
     * @param userId ID of the handyman
     * @return a list of {@link Review} entities for the handyman
     */
    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepo.findByReviewedHandymanId(userId);
    }

    /**
     * Converts a {@link Review} entity into a {@link ReviewResponse} DTO.
     *
     * @param review the review entity to convert
     * @return a {@link ReviewResponse} containing review details
     */
    private ReviewResponse mapToDto(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setReviewerName(review.getReviewer().getName());
        dto.setReviewedHandymanName(review.getReviewedHandyman().getName());

        return dto;
    }

    /**
     * Retrieves all reviews associated with a specific task.
     *
     * @param taskId ID of the task
     * @return a list of {@link ReviewResponse} DTOs
     */
    public List<ReviewResponse> getReviewsForTask(Long taskId) {
        List<Review> reviews = reviewRepo.findByTaskId(taskId);
        return reviews.stream()
                .map(this::mapToDto)
                .toList();
    }
}
