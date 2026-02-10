package com.handyman.oddhandyman.review.entity;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Schema(description = "Represents a review submitted by a customer for a handyman after completing a task")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the review", example = "1")
    private Long id;

    @Schema(description = "Rating given by the reviewer (1-5)", example = "5")
    private int rating;

    @Column(length = 2000)
    @Schema(description = "Optional textual comment provided by the reviewer", example = "Excellent work, very professional!")
    private String comment;

    @ManyToOne
    @Schema(description = "User who submitted the review (customer)")
    private User reviewer;

    @ManyToOne
    @Schema(description = "Handyman being reviewed")
    private User reviewedHandyman;

    @OneToOne
    @Schema(description = "Task associated with this review")
    private Task task;

    @Schema(description = "Timestamp when the review was created", example = "2026-02-10T15:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "Timestamp when the review was last updated", example = "2026-02-10T16:00:00")
    private LocalDateTime updatedAt;

    // --- Getters and Setters ---

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

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    public User getReviewedHandyman() {
        return reviewedHandyman;
    }

    public void setReviewedHandyman(User reviewedHandyman) {
        this.reviewedHandyman = reviewedHandyman;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
