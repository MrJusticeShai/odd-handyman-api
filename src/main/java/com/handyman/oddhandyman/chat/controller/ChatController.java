package com.handyman.oddhandyman.chat.controller;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.chat.dto.ChatMessageRequest;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling chat messages between customers and handymen.
 * <p>
 * Exposes endpoints for sending messages, retrieving messages for a task,
 * marking messages as read, and fetching unread message counts.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Send a message in a task chat", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(
            @Valid @RequestBody ChatMessageRequest req,
            Authentication auth
    ) {
        return ResponseEntity.ok(chatService.sendMessage(req, auth.getName()));
    }

    @Operation(summary = "Get all messages for a task",
            description = "Retrieves all chat messages for a specific task, ordered by timestamp ascending.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long taskId) {
        return ResponseEntity.ok(chatService.getMessagesForTask(taskId));
    }

    @Operation(summary = "Mark all messages as read for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/mark-read/{taskId}")
    public ResponseEntity<?> markRead(
            @PathVariable Long taskId,
            Authentication auth
    ) {
        chatService.markMessagesAsRead(taskId, auth.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get unread message counts for the current user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/unread-count")
    public Map<Long, Long> getUnreadCounts(
            @RequestParam String email,
            @RequestParam Role role
    ) {
        if (role != Role.CUSTOMER && role != Role.HANDYMAN) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        return chatService.getUnreadCounts(email, role);
    }


}
