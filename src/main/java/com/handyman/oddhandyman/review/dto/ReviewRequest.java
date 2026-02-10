package com.handyman.oddhandyman.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to submit a review for a completed task.
 */
@Schema(description = "Payload for submitting a review for a task")
public class ReviewRequest {

    @NotNull
    @Schema(
            description = "ID of the task being reviewed",
            example = "123"
    )
    private Long taskId;

    @Min(1)
    @Max(5)
    @Schema(
            description = "Rating given for the task, typically 1-5",
            example = "4"
    )
    private int rating;

    @Schema(
            description = "Optional comment about the task or handyman",
            example = "Excellent work, arrived on time and completed everything!"
    )
    private String comment;

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
}
