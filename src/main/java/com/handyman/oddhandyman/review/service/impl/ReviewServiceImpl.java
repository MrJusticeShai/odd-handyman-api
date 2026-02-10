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

    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepo.findByReviewedHandymanId(userId);
    }

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

    public List<ReviewResponse> getReviewsForTask(Long taskId) {
        List<Review> reviews = reviewRepo.findByTaskId(taskId);
        return reviews.stream()
                .map(this::mapToDto)
                .toList();
    }
}
