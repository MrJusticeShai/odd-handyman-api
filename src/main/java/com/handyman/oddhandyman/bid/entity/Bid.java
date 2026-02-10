package com.handyman.oddhandyman.bid.entity;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.bid.entity.enums.BidStatus;
import com.handyman.oddhandyman.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a bid placed by a handyman on a task.
 * <p>
 * Each bid is associated with a task and a handyman (user), has a monetary amount,
 * a status, and a creation timestamp. The status tracks whether the bid is pending,
 * accepted, or rejected by the task owner.
 */
@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the bid", example = "1")
    private Long id;

    @Schema(description = "Monetary amount proposed by the handyman", example = "50.00")
    private Double amount;

    @ManyToOne
    @Schema(description = "Task for which this bid is placed")
    private Task task;

    @ManyToOne
    @Schema(description = "Handyman (user) who placed the bid")
    private User handyman;

    @Enumerated(EnumType.STRING)
    @Schema(
            description = "Current status of the bid",
            example = "PENDING",
            allowableValues = { "PENDING", "ACCEPTED", "REJECTED" }
    )
    private BidStatus status = BidStatus.PENDING;

    @Schema(description = "Timestamp when the bid was created", example = "2026-02-10T14:30:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getHandyman() {
        return handyman;
    }

    public void setHandyman(User handyman) {
        this.handyman = handyman;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
