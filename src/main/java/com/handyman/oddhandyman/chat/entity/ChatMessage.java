package com.handyman.oddhandyman.chat.entity;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a chat message exchanged between customer and handyman for a specific task.
 */
@Entity
@Table(name = "chat_messages")
@Schema(description = "Entity representing a chat message exchanged between a customer and handyman for a task")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the chat message", example = "1")
    private Long id;

    @ManyToOne
    @Schema(description = "Task associated with this chat message")
    private Task task;

    @ManyToOne
    @Schema(description = "User who sent the message")
    private User sender;

    @Column(length = 1000)
    @Schema(description = "Content of the message", example = "Hello, I have completed the task.")
    private String message;

    @Schema(description = "Timestamp when the message was sent")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Indicates if the customer has read the message", example = "false")
    private boolean readByCustomer = false;

    @Schema(description = "Indicates if the handyman has read the message", example = "false")
    private boolean readByHandyman = false;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReadByCustomer() {
        return readByCustomer;
    }

    public void setReadByCustomer(boolean readByCustomer) {
        this.readByCustomer = readByCustomer;
    }

    public boolean isReadByHandyman() {
        return readByHandyman;
    }

    public void setReadByHandyman(boolean readByHandyman) {
        this.readByHandyman = readByHandyman;
    }
}
