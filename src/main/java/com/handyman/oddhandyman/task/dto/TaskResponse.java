package com.handyman.oddhandyman.task.dto;

import com.handyman.oddhandyman.auth.dto.UserResponse;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object representing a task returned from the API.
 * <p>
 * This DTO is used to present tasks to the frontend, including task details,
 * status, customer information, and optionally the assigned handyman.
 */
@Schema(description = "Response payload representing a task, including status, customer, and assigned handyman")
public class TaskResponse {

    @Schema(description = "Unique identifier of the task", example = "1")
    private Long id;

    @Schema(description = "Title or short summary of the task", example = "Fix leaking faucet")
    private String title;

    @Schema(description = "Detailed description of the task", example = "The kitchen faucet is leaking and needs repair")
    private String description;

    @Schema(description = "Address where the task needs to be completed", example = "123 Main Street, Cape Town")
    private String address;

    @Schema(description = "Budget allocated for the task", example = "150.00")
    private Double budget;

    @Schema(description = "Deadline for task completion", example = "2026-02-15T17:00:00")
    private LocalDateTime deadline;

    @Schema(description = "Current status of the task", example = "PENDING")
    private TaskStatus status;

    @Schema(description = "Customer who created the task")
    private UserResponse customer;

    @Schema(description = "Handyman assigned to the task, if any")
    private UserResponse assignedHandyman;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UserResponse getCustomer() {
        return customer;
    }

    public void setCustomer(UserResponse customer) {
        this.customer = customer;
    }

    public UserResponse getAssignedHandyman() {
        return assignedHandyman;
    }

    public void setAssignedHandyman(UserResponse assignedHandyman) {
        this.assignedHandyman = assignedHandyman;
    }
}
