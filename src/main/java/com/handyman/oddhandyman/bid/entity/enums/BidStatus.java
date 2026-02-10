package com.handyman.oddhandyman.bid.entity.enums;

/**
 * Enumeration representing the possible statuses of a bid in the system.
 * <p>
 * Each bid submitted by a handyman for a task will have one of these statuses:
 * <ul>
 *     <li>{@link #PENDING} — Bid has been submitted but not yet reviewed or accepted.</li>
 *     <li>{@link #ACCEPTED} — Bid has been approved by the task owner.</li>
 *     <li>{@link #REJECTED} — Bid has been declined by the task owner.</li>
 * </ul>
 */
public enum BidStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
