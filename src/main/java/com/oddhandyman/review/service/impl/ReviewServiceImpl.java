package com.oddhandyman.review.service.impl;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.repository.UserRepository;
import com.oddhandyman.review.dto.ReviewRequest;
import com.oddhandyman.review.dto.ReviewResponse;
import com.oddhandyman.review.entity.Review;
import com.oddhandyman.review.entity.ReviewImage;
import com.oddhandyman.review.repository.ReviewRepository;
import com.oddhandyman.review.service.ReviewService;
import com.oddhandyman.task.entity.Task;
import com.oddhandyman.task.entity.enums.TaskStatus;
import com.oddhandyman.task.repository.TaskRepository;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepo.findById(req.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCustomer().getId().equals(reviewer.getId())) {
            throw new RuntimeException("You cannot review someone else's task!");
        }

        if (task.getStatus() != TaskStatus.COMPLETED) {
            throw new RuntimeException("Cannot leave review until task is completed.");
        }

        if (reviewRepo.existsByTaskId(task.getId())) {
            throw new RuntimeException("This task already has a review.");
        }

        Review review = new Review();
        review.setTask(task);
        review.setReviewer(reviewer);
        review.setReviewedHandyman(task.getAssignedHandyman());
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        // Attach images
        if (req.getImageUrls() != null) {
            List<ReviewImage> imgs = req.getImageUrls().stream()
                    .map(url -> {
                        ReviewImage img = new ReviewImage();
                        img.setImageUrl(url);
                        img.setReview(review);
                        return img;
                    }).toList();

            review.setImages(imgs);
        }

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

        List<String> urls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList();
        dto.setImages(urls);

        return dto;
    }

    public List<ReviewResponse> getReviewsForTask(Long taskId) {
        List<Review> reviews = reviewRepo.findByTaskId(taskId);
        return reviews.stream()
                .map(this::mapToDto)
                .toList();
    }


}
