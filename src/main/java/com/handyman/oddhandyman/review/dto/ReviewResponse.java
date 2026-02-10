package com.handyman.oddhandyman.review.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private int rating;
    private String comment;
    private String reviewerName;
    private String reviewedHandymanName;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewedHandymanName() {
        return reviewedHandymanName;
    }

    public void setReviewedHandymanName(String reviewedHandymanName) {
        this.reviewedHandymanName = reviewedHandymanName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
