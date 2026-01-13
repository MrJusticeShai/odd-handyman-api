package com.oddhandyman.review.repository;

import com.oddhandyman.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Check if a task already has a review
    boolean existsByTaskId(Long taskId);

    // Fetch all reviews for a specific task
    List<Review> findByTaskId(Long taskId);

    // Fetch reviews written about a specific handyman
    List<Review> findByReviewedHandymanId(Long userId);

    // Fetch review written by a specific user on a specific task (to prevent duplicates)
    Review findByTaskIdAndReviewerId(Long taskId, Long reviewerId);
}
