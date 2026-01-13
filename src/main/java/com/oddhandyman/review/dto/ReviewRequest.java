package com.oddhandyman.review.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class ReviewRequest {
    private Long taskId;
    private int rating;
    private String comment;
    private List<String> imageUrls; // Sent after upload

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
