package com.handyman.oddhandyman.review.service.impl;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.exception.UnacceptableOperationException;
import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;
import com.handyman.oddhandyman.review.repository.ReviewRepository;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepo;
    @Mock private TaskRepository taskRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks private ReviewServiceImpl reviewService;

    private User mockCustomer;
    private User mockHandyman;
    private Task mockTask;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        mockCustomer = new User();
        mockCustomer.setId(1L);
        mockCustomer.setEmail("customer@test.com");
        mockCustomer.setName("John Customer");

        mockHandyman = new User();
        mockHandyman.setId(2L);
        mockHandyman.setName("Bob Handyman");

        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setCustomer(mockCustomer);
        mockTask.setAssignedHandyman(mockHandyman);
        mockTask.setStatus(TaskStatus.COMPLETED);

        reviewRequest = new ReviewRequest();
        reviewRequest.setTaskId(100L);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great work!");
    }

    @Nested
    @DisplayName("createReview() Logic")
    class CreateReviewTests {

        @Test
        @DisplayName("Positive: Should create review when all conditions are met")
        void createReview_Success() {
            when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockCustomer));
            when(taskRepo.findById(100L)).thenReturn(Optional.of(mockTask));
            when(reviewRepo.existsByTaskId(100L)).thenReturn(false);
            when(reviewRepo.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

            Review result = reviewService.createReview(reviewRequest, "customer@test.com");

            assertNotNull(result);
            assertEquals(mockHandyman, result.getReviewedHandyman());
            assertEquals(5, result.getRating());
            verify(reviewRepo).save(any(Review.class));
        }

        @Test
        @DisplayName("Negative: Should throw exception if task is not COMPLETED")
        void createReview_TaskNotCompleted_Fails() {
            mockTask.setStatus(TaskStatus.ASSIGNED);
            when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockCustomer));
            when(taskRepo.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(UnacceptableOperationException.class, () ->
                    reviewService.createReview(reviewRequest, "customer@test.com"));
        }

        @Test
        @DisplayName("Negative: Should throw exception if reviewer is not the task owner")
        void createReview_WrongCustomer_Fails() {
            User wrongCustomer = new User();
            wrongCustomer.setId(99L);
            when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(wrongCustomer));
            when(taskRepo.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(UnacceptableOperationException.class, () ->
                    reviewService.createReview(reviewRequest, "wrong@test.com"));
        }

        @Test
        @DisplayName("Negative: Should throw exception if task already has a review")
        void createReview_Duplicate_Fails() {
            when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockCustomer));
            when(taskRepo.findById(100L)).thenReturn(Optional.of(mockTask));
            when(reviewRepo.existsByTaskId(100L)).thenReturn(true);

            assertThrows(UnacceptableOperationException.class, () ->
                    reviewService.createReview(reviewRequest, "customer@test.com"));
        }
    }

    @Nested
    @DisplayName("getReviewsForTask() Logic")
    class GetReviewsTests {

        @Test
        @DisplayName("Positive: Should map Review entities to ReviewResponse DTOs")
        void getReviewsForTask_Success() {
            Review review = new Review();
            review.setId(1L);
            review.setRating(5);
            review.setComment("Nice!");
            review.setReviewer(mockCustomer);
            review.setReviewedHandyman(mockHandyman);

            when(reviewRepo.findByTaskId(100L)).thenReturn(Collections.singletonList(review));

            List<ReviewResponse> results = reviewService.getReviewsForTask(100L);

            assertEquals(1, results.size());
            assertEquals("John Customer", results.get(0).getReviewerName());
            assertEquals("Bob Handyman", results.get(0).getReviewedHandymanName());
        }
    }
}
