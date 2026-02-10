package com.handyman.oddhandyman.task.entity.enums;

/**
 * Represents the current state of a task in the system.
 * <p>
 *  <ul>
 *   <li>{@link #PENDING} - PENDING: Task has been created but no handyman is assigned yet.
 *   <li>{@link #ASSIGNED} - ASSIGNED: Task has been assigned to a handyman.
 *   <li>{@link #COMPLETED} - COMPLETED: Task has been completed successfully.
 *   <li>{@link #CANCELLED} - CANCELLED: Task has been cancelled by the customer or system.
 * </ul>
 */
public enum TaskStatus {
    PENDING,
    ASSIGNED,
    COMPLETED,
    CANCELLED
}
