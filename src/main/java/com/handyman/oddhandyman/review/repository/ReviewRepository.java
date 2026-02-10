package com.handyman.oddhandyman.review.repository;

import com.handyman.oddhandyman.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Review} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard JPA operations (save, findById, delete, etc.)
 * and declares custom query methods specific to reviews.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Checks whether a review already exists for a given task.
     * Useful for preventing duplicate reviews for the same task.
     *
     * @param taskId ID of the task
     * @return {@code true} if a review exists for the task, {@code false} otherwise
     */
    boolean existsByTaskId(Long taskId);

    /**
     * Retrieves all reviews associated with a specific task.
     *
     * @param taskId ID of the task
     * @return a list of {@link Review} entities for the specified task
     */
    List<Review> findByTaskId(Long taskId);

    /**
     * Retrieves all reviews written about a specific handyman.
     *
     * @param userId ID of the handyman
     * @return a list of {@link Review} entities written for the handyman
     */
    List<Review> findByReviewedHandymanId(Long userId);

    /**
     * Retrieves a review submitted by a specific reviewer for a specific task.
     * Helps prevent duplicate reviews by the same reviewer.
     *
     * @param taskId ID of the task
     * @param reviewerId ID of the reviewer
     * @return the {@link Review} entity if it exists, or {@code null} otherwise
     */
    Review findByTaskIdAndReviewerId(Long taskId, Long reviewerId);
}
