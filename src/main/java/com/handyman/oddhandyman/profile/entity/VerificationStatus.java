package com.handyman.oddhandyman.profile.entity;

/**
 * Enumeration representing the verification state of a handyman's profile in the system.
 * <p>
 * Each profile will have one of these statuses to indicate its current verification progress:
 * <ul>
 *     <li>{@link #PENDING} — Verification has not yet been initiated.</li>
 *     <li>{@link #SUBMITTED} — Profile has been submitted for admin review.</li>
 *     <li>{@link #APPROVED} — Profile has been approved by an admin.</li>
 *     <li>{@link #REJECTED} — Profile has been rejected by an admin.</li>
 * </ul>
 */
public enum VerificationStatus {
    PENDING,
    SUBMITTED,
    APPROVED,
    REJECTED
}

