package com.handyman.oddhandyman.task.entity;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a task created by a customer in the system.
 * <p>
 * A task has a title, description, budget, deadline, and status. It is associated
 * with a customer who created it and may be assigned to a handyman when accepted.
 * Status values are defined in {@link TaskStatus}.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the task")
    private Long id;

    @Schema(description = "Short title or summary of the task" )
    private String title;

    @Schema(description = "Detailed description of the task; maximum length 1000 characters" )
    @Column(length = 1000)
    private String description;

    @Schema(description = "Physical address where the task needs to be completed" )
    private String address;

    @Schema(description = "Budget allocated for the task" )
    private Double budget;

    @Schema(description = "Deadline for task completion" )
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Current status of the task; defaults to PENDING" )
    private TaskStatus status = TaskStatus.PENDING;

    @ManyToOne
    @Schema(description = "Customer who created the task" )
    private User customer;

    @ManyToOne
    @Schema(description = "Handyman assigned to the task (nullable until assigned)" )
    private User assignedHandyman;

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

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getAssignedHandyman() {
        return assignedHandyman;
    }

    public void setAssignedHandyman(User assignedHandyman) {
        this.assignedHandyman = assignedHandyman;
    }
}
