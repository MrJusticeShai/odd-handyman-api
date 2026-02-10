package com.handyman.oddhandyman.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request to place or update a bid for a specific task.
 * <p>
 * Used in API endpoints where a handyman proposes an amount for a task.
 * Validation ensures that the task ID is provided and the bid amount is non-negative.
 */
@Schema(
        description = "Request payload used to place or update bid for a task"
)
public class BidRequest {

    @NotNull(message = "taskId is required")
    @Schema(
            description = "Unique identifier of the task being bid on",
            example = "1"
    )
    private Long taskId;

    @NotNull
    @Min(value = 0, message = "Amount cannot be negative")
    @Schema(
            description = "The monetary amount proposed in the bid",
            example = "10.00"
    )
    private Double amount;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
