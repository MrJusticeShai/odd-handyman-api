package com.handyman.oddhandyman.auth.entity;

/**
 * Enumeration representing the roles a user can have in the system.
 * <p>
 * Roles determine what functionality a user has access to:
 * <ul>
 *     <li>{@link #CUSTOMER} — Regular customer who can create tasks and view bids.</li>
 *     <li>{@link #HANDYMAN} — Handyman who can place bids on tasks.</li>
 *     <li>{@link #ADMIN} — System administrator with elevated permissions.</li>
 * </ul>
 */
public enum Role {
    CUSTOMER,
    HANDYMAN,
    ADMIN
}
