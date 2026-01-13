package com.oddhandyman.review.service;

import com.oddhandyman.review.dto.ReviewRequest;
import com.oddhandyman.review.dto.ReviewResponse;
import com.oddhandyman.review.entity.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(ReviewRequest req, String userEmail);

    List<Review> getReviewsForUser(Long userId);

    List<ReviewResponse> getReviewsForTask(Long taskId);

}
