package com.handyman.oddhandyman.review.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.review.entity.Review;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User customer;
    private User handyman;
    private Task task;

    @BeforeEach
    void setUp() {
        // 1. Setup Users
        customer = new User();
        customer.setEmail("customer@test.com");
        customer.setName("John Customer");
        customer.setRole(Role.CUSTOMER);
        customer.setPassword("pass");
        entityManager.persist(customer);

        handyman = new User();
        handyman.setEmail("handy@test.com");
        handyman.setName("Bob Handyman");
        handyman.setRole(Role.HANDYMAN);
        handyman.setPassword("pass");
        entityManager.persist(handyman);

        // 2. Setup Completed Task
        task = new Task();
        task.setTitle("Fix Fence");
        task.setCustomer(customer);
        task.setAssignedHandyman(handyman);
        task.setStatus(TaskStatus.COMPLETED);
        entityManager.persist(task);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should return true if a review exists for a specific task ID")
    void existsByTaskId_ReturnsTrue() {
        // Given
        Review review = new Review();
        review.setTask(task);
        review.setReviewer(customer);
        review.setReviewedHandyman(handyman);
        review.setRating(5);
        entityManager.persist(review);
        entityManager.flush();

        // When
        boolean exists = reviewRepository.existsByTaskId(task.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should find all reviews for a specific handyman by user ID")
    void findByReviewedHandymanId_ReturnsReviews() {
        // Given
        Review review = new Review();
        review.setTask(task);
        review.setReviewer(customer);
        review.setReviewedHandyman(handyman);
        review.setRating(4);
        entityManager.persist(review);
        entityManager.flush();

        // When
        List<Review> results = reviewRepository.findByReviewedHandymanId(handyman.getId());

        // Then
        assertEquals(1, results.size());
        assertEquals("Bob Handyman", results.get(0).getReviewedHandyman().getName());
    }

    @Test
    @DisplayName("Should find a review by specific task ID and reviewer ID")
    void findByTaskIdAndReviewerId_ReturnsReview() {
        // Given
        Review review = new Review();
        review.setTask(task);
        review.setReviewer(customer);
        review.setReviewedHandyman(handyman);
        review.setRating(5);
        entityManager.persist(review);
        entityManager.flush();

        // When
        Review found = reviewRepository.findByTaskIdAndReviewerId(task.getId(), customer.getId());

        // Then
        assertNotNull(found);
        assertEquals(5, found.getRating());
    }

    @Test
    @DisplayName("Should return empty results when no review exists for the task")
    void findByTaskId_ReturnsEmptyList() {
        // When
        List<Review> results = reviewRepository.findByTaskId(task.getId());

        // Then
        assertTrue(results.isEmpty());
    }
}
