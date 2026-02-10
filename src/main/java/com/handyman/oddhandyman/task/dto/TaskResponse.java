package com.handyman.oddhandyman.task.dto;

import com.handyman.oddhandyman.auth.dto.UserResponse;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private Double budget;
    private LocalDateTime deadline;
    private TaskStatus status;
    private UserResponse customer;
    private UserResponse assignedHandyman;

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
