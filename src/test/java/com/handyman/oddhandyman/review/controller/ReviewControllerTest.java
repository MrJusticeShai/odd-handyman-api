package com.handyman.oddhandyman.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;
import com.handyman.oddhandyman.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private Review fullReview;
    private ReviewResponse reviewResponse;
    private final String TEST_EMAIL = "customer@example.com";

    @BeforeEach
    void setUp() {
        fullReview = new Review();
        fullReview.setId(1L);
        fullReview.setRating(5);
        fullReview.setComment("Excellent service!");
        fullReview.setCreatedAt(LocalDateTime.now());

        reviewResponse = new ReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Excellent service!");
        reviewResponse.setReviewerName("John Doe");
    }

    @Nested
    @DisplayName("POST /api/reviews - Create Review")
    class CreateReviewTests {

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Positive: Should submit review and return 200")
        void createReview_Success() throws Exception {
            ReviewRequest req = new ReviewRequest();
            req.setTaskId(100L);
            req.setRating(5);
            req.setComment("Great job!");

            when(reviewService.createReview(any(ReviewRequest.class), eq(TEST_EMAIL)))
                    .thenReturn(fullReview);

            mockMvc.perform(post("/api/reviews")
                            .with(csrf()) // CSRF required when filters are ON
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.comment").value("Excellent service!"));
        }

        @Test
        @DisplayName("Negative: Should return 401 Unauthorized without session")
        void createReview_NoAuth_Fails() throws Exception {
            mockMvc.perform(post("/api/reviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Negative: Should return 400 when validation fails")
        void createReview_InvalidRequest_Fails() throws Exception {
            ReviewRequest invalidReq = new ReviewRequest();
            invalidReq.setRating(10); // Assuming @Max(5) in DTO

            mockMvc.perform(post("/api/reviews")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidReq)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/task/{taskId}")
    class ReviewsForTaskTests {

        @Test
        @WithMockUser
        @DisplayName("Positive: Should return list of review responses")
        void reviewsForTask_Success() throws Exception {
            when(reviewService.getReviewsForTask(100L))
                    .thenReturn(Collections.singletonList(reviewResponse));

            mockMvc.perform(get("/api/reviews/task/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].reviewerName").value("John Doe"));
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/user/{userId}")
    class ReviewsForUserTests {

        @Test
        @WithMockUser
        @DisplayName("Positive: Should return reviews for specific user")
        void reviewsForUser_Success() throws Exception {
            when(reviewService.getReviewsForUser(1L))
                    .thenReturn(Arrays.asList(fullReview));

            mockMvc.perform(get("/api/reviews/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));
        }
    }
}
