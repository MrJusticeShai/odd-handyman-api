package com.oddhandyman.task.entity.enums;

public enum TaskStatus {
    PENDING,      // task created, no handyman assigned
    ASSIGNED,     // task assigned to handyman
    COMPLETED,    // task completed
    CANCELLED     // task cancelled
}
