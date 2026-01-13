package com.oddhandyman.bid.entity;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.bid.entity.enums.BidStatus;
import com.oddhandyman.task.entity.Task;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User handyman;

    @Enumerated(EnumType.STRING)
    private BidStatus status = BidStatus.PENDING;

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
