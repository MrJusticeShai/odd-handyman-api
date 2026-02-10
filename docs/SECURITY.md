# Security Model – Odd-Handy-Man

## Purpose of This Document

This document describes the **security and authorization model** of Odd-Handy-Man at a high level.

It focuses on **rules, guarantees, and design intent**, not on framework-specific implementation details.

---

## Security Goals

Odd-Handy-Man is designed to:

* Protect user identities and data
* Ensure only authorized users can perform actions
* Prevent access to tasks, chats, and reviews by unrelated parties
* Reduce disputes through enforced rules rather than trust

Security is treated as a **core product feature**, not an afterthought.

---

## Authentication Model

The platform uses **stateless authentication**.

* Users authenticate with credentials
* A signed token is issued
* The token is required for all protected actions

This approach:

* Scales horizontally
* Avoids server-side session state
* Keeps the API simple and predictable

---

## User Roles

There are two system roles:

* **Customer** – creates tasks and reviews completed work
* **Handyman** – bids on tasks and performs assigned work

Roles define *capabilities*, but **never override ownership rules**.

---

## Authorization Dimensions

Every protected action is evaluated across **three dimensions**:

1. **Role** – Is the user allowed to perform this type of action?
2. **Ownership** – Is the user involved in this specific resource?
3. **Task State** – Is the action valid at the current stage of the task lifecycle?

An action is permitted **only if all three conditions are satisfied**.

---

## Task Ownership Rules

Tasks have strict ownership boundaries:

* Only the customer who created a task can:

    * Accept a bid
    * Assign a handyman
    * Review a completed task

* Only the assigned handyman can:

    * Work on the task
    * Mark it as completed

No other users can view or interact with the task.

---

## Task State Enforcement

The task lifecycle directly controls what actions are allowed:

* **PENDING**

    * Customers may negotiate and accept bids
    * Handymen may place or update bids

* **ASSIGNED**

    * Task is in progress
    * No further negotiation is allowed
    * Chat remains writable

* **COMPLETED**

    * Task is closed
    * Chat becomes read-only
    * Review may be submitted

Invalid state transitions are rejected by the system.

---

## Communication Security

Chat is tightly scoped:

* Messages are linked to a specific task
* Only the customer and assigned handyman can participate
* Messages cannot be edited or deleted
* Chat is locked after task completion

This creates a permanent, auditable communication trail.

---

## Review Security & Visibility

Reviews follow strict rules:

* Only customers may submit reviews
* Reviews are tied to a completed task
* Reviews are visible **only** to:

    * The customer
    * The assigned handyman

There is no public rating system.

This design favors accountability over public reputation scoring.

---

## Data Access Boundaries

The system enforces **resource-level access control**:

* Tasks, chats, bids, and reviews are never globally accessible
* All data access is scoped to the authenticated user
* Enumeration of other users’ data is prevented by design

---

## Threat Reduction by Design

Several architectural decisions reduce security risk:

* No public profiles
* No open messaging
* No anonymous actions
* No payments in early stages

By limiting surface area, the platform reduces both technical and social attack vectors.

---

## Security Principles Summary

Odd-Handy-Man security is built on:

* Explicit permissions
* Strong ownership rules
* State-driven authorization
* Minimal exposure

The goal is not just to block attackers, but to **prevent ambiguity and misuse altogether**.
