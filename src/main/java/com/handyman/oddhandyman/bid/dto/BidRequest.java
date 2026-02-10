package com.handyman.oddhandyman.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request payload used to place or update bid for a task"
)
public class BidRequest {

    @NotBlank
    @Schema(
            description = "Unique identifier for a task",
            example = "1"
    )
    private Long taskId;

    @NotNull
    @Min(value = 0, message = "Amount cannot be negative")
    @Schema(
            description = "The monetary amount to be bid or proposed",
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
