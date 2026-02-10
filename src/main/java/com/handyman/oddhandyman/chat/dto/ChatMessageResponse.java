package com.handyman.oddhandyman.chat.dto;

import com.handyman.oddhandyman.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response payload for a chat message associated with a task.
 */
@Schema(description = "Response payload for a chat message associated with a task")
public class ChatMessageResponse {

    @Schema(description = "Unique ID of the chat message", example = "101")
    private Long id;

    @Schema(description = "ID of the task this message belongs to", example = "123")
    private Long taskId;

    @Schema(description = "User who sent the message")
    private User sender;

    @Schema(description = "Content of the chat message", example = "Hello, I am ready to start the task.")
    private String message;

    @Schema(description = "Timestamp when the message was sent", example = "2026-02-10T15:30:00")
    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
}
