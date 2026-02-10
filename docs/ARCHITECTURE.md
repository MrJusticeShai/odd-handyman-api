# Architecture Overview – Odd-Handy-Man

## Purpose of This Document

This document explains the **architectural decisions and design philosophy** behind Odd-Handy-Man at a system level.

It intentionally avoids low-level implementation details and focuses instead on **why the system is structured the way it is**, what trade-offs were made, and how those decisions support the product’s goals.

---

## System Goals

Odd-Handy-Man is designed to:

* Connect skilled handymen with customers who need small-to-medium tasks completed
* Reduce friction in finding work and getting reliable help
* Support employment opportunities without upfront financial barriers
* Minimize disputes through clear ownership and task states

The architecture prioritizes **clarity, correctness, and evolvability** over premature scalability.

---

## High-Level System View

At a high level, Odd-Handy-Man consists of:

* A **RESTful API** that enforces business rules and task lifecycle
* A **web client** that consumes the API
* A **relational database** that maintains strong consistency

All business-critical decisions live on the server.

---

## Feature-Based Modular Design

The backend is organized by **business capability**, not by technical layer.

Each feature (e.g. authentication, tasks, bids, chat, reviews) owns:

* Its domain model
* Its service logic
* Its data access
* Its API surface

This structure:

* Improves readability
* Limits cross-feature coupling
* Makes future changes safer and more localized

---

## Task-Centric Architecture

The **task** is the core aggregate of the system.

Everything else is scoped to a task:

* Bids exist *for a task*
* Chats exist *for a task*
* Reviews exist *for a completed task*

This ensures:

* Clear ownership
* Strong authorization boundaries
* Easier reasoning about permissions

---

## Task Lifecycle as a State Machine

Odd-Handy-Man uses a **small, explicit task lifecycle**:

* **PENDING** – Task created, open for bids and negotiation
* **ASSIGNED** – Handyman selected, work in progress
* **COMPLETED** – Work finished, system closed

All system behavior is driven by this lifecycle:

* Negotiation is only allowed in `PENDING`
* Chat becomes read-only in `COMPLETED`
* Reviews are only allowed after completion

By encoding business rules into task state, the system avoids ambiguous behavior.

---

## Negotiation Model

Negotiation is intentionally simple:

* All negotiation happens through bids while the task is `PENDING`
* Once a bid is accepted, the task moves to `ASSIGNED`
* No renegotiation is allowed after assignment

This reduces:

* Disputes
* Scope creep
* Unclear expectations

---

## Communication Model

Chat is **task-scoped** and **participant-restricted**:

* Only the assigned handyman and the task owner can chat
* Messages are immutable
* Chat becomes read-only once the task is `COMPLETED`

This design:

* Preserves an audit trail
* Prevents post-completion disputes
* Keeps communication focused on delivery

---

## Review Visibility Model

Reviews are:

* Written only by the customer
* Linked to a specific completed task
* Visible only to the involved customer and handyman

This avoids:

* Public shaming
* Reputation gaming
* Review inflation

The goal is **accountability, not ranking**.

---

## Authentication & Authorization Philosophy

The system uses **stateless authentication**.

Authorization is based on:

* User role (Customer vs Handyman)
* Resource ownership (task participation)
* Task state

Every protected action must satisfy all three dimensions.

---

## Data Consistency Strategy

Odd-Handy-Man favors **strong consistency** over eventual consistency:

* Relational database
* Transactional updates
* Explicit state transitions

This is appropriate given:

* Financial implications of work
* Low tolerance for ambiguity
* Relatively low write volume

---

## Why Payments Are Excluded (For Now)

Payments are intentionally not part of the initial architecture.

Reasons:

* Payment disputes add significant complexity
* Trust must be established first
* Legal and regulatory concerns vary by region

The architecture leaves room for future payment integration without coupling it to core task logic.

---

## Scalability & Evolution

The system is designed to scale **incrementally**:

* Clear module boundaries
* Stateless API
* Replaceable infrastructure components

Future enhancements may include:

* Payment services
* Notifications
* Search & discovery
* Mobile clients

These can be added without reworking the core domain model.

---

## Architectural Principles Summary

Odd-Handy-Man follows these principles:

* Explicit state over implicit behavior
* Clear ownership boundaries
* Simplicity before scale
* Business rules enforced centrally

The result is a system that is easier to understand, maintain, and extend.
