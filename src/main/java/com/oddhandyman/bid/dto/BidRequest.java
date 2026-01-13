package com.oddhandyman.bid.dto;

import jakarta.validation.constraints.NotNull;

public class BidRequest {
    @NotNull
    private Long taskId;

    @NotNull
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
