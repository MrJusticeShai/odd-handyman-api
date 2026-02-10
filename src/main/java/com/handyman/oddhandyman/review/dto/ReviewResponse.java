package com.handyman.oddhandyman.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response DTO representing a review left for a handyman.
 * <p>
 * Contains the review details, reviewer information, the handyman reviewed, and timestamp.
 */
@Schema(description = "Response returned when fetching a review for a handyman")
public class ReviewResponse {

    @Schema(
            description = "Unique identifier of the review",
            example = "101"
    )
    private Long id;

    @Schema(
            description = "Rating given by the reviewer, typically on a scale of 1 to 5",
            example = "5"
    )
    private int rating;

    @Schema(
            description = "Optional textual comment about the task or handyman",
            example = "Completed the task on time and did an excellent job!"
    )
    private String comment;

    @Schema(
            description = "Name of the user who submitted the review",
            example = "John Doe"
    )
    private String reviewerName;

    @Schema(
            description = "Name of the handyman who was reviewed",
            example = "Jane Smith"
    )
    private String reviewedHandymanName;

    @Schema(
            description = "Timestamp when the review was created",
            example = "2026-02-10T14:30:00"
    )
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
