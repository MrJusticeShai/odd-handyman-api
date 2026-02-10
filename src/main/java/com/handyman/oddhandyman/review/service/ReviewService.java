package com.handyman.oddhandyman.review.service;

import com.handyman.oddhandyman.review.dto.ReviewRequest;
import com.handyman.oddhandyman.review.dto.ReviewResponse;
import com.handyman.oddhandyman.review.entity.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(ReviewRequest req, String userEmail);

    List<Review> getReviewsForUser(Long userId);

    List<ReviewResponse> getReviewsForTask(Long taskId);

}
