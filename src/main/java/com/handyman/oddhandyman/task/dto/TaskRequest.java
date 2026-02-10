package com.handyman.oddhandyman.task.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a task.
 * <p>
 * This payload is sent when a customer wants to create a new task in the system.
 * Validation ensures all required fields are provided.
 */
@Schema(description = "Request payload used to create or update a task")
public class TaskRequest {

    @NotBlank
    @Schema(
            description = "Title or short summary of the task",
            example = "Fix leaking faucet",
            required = true
    )
    private String title;

    @NotBlank
    @Schema(
            description = "Detailed description of the task",
            example = "The kitchen faucet is leaking and needs repair",
            required = true
    )
    private String description;

    @NotBlank
    @Schema(
            description = "Physical address where the task needs to be completed",
            example = "123 Main Street, Naboomspruite",
            required = true
    )
    private String address;

    @NotNull
    @Schema(
            description = "Budget allocated for the task in monetary units",
            example = "150.00",
            required = true
    )
    private Double budget;

    @NotNull
    @Schema(
            description = "Deadline for completing the task in ISO-8601 format",
            example = "2026-02-15T17:00:00",
            required = true
    )
    private LocalDateTime deadline;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}

