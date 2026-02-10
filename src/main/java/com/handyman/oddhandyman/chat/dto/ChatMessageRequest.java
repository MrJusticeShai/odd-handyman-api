package com.handyman.oddhandyman.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for sending a chat message related to a specific task.
 */
@Schema(description = "Request payload for sending a chat message related to a task")
public class ChatMessageRequest {

    @NotNull
    @Schema(
            description = "ID of the task this message is related to",
            example = "123"
    )
    private Long taskId;

    @NotBlank
    @Schema(
            description = "Content of the chat message",
            example = "Hello, I am available to start the task tomorrow."
    )
    private String message;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
