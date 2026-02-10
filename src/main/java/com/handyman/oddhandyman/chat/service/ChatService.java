package com.handyman.oddhandyman.chat.service;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.chat.dto.ChatMessageRequest;
import com.handyman.oddhandyman.chat.entity.ChatMessage;

import java.util.List;
import java.util.Map;

/**
 * Service interface for handling chat messages between customers and handymen.
 */
public interface ChatService {

    /**
     * Sends a new chat message for a specific task.
     *
     * @param req the {@link ChatMessageRequest} containing taskId and message content
     * @param senderEmail email of the user sending the message
     * @return the saved {@link ChatMessage} entity
     */
    ChatMessage sendMessage(ChatMessageRequest req, String senderEmail);

    /**
     * Retrieves all chat messages for a given task, ordered by timestamp ascending.
     *
     * @param taskId the ID of the task
     * @return list of {@link ChatMessage} for the task
     */
    List<ChatMessage> getMessagesForTask(Long taskId);

    /**
     * Marks messages as read for a specific task and user.
     * <p>
     * If the user is a customer, it marks all messages from the handyman as read.
     * If the user is a handyman, it marks all messages from the customer as read.
     *
     * @param taskId the ID of the task
     * @param userEmail email of the user marking messages as read
     */
    void markMessagesAsRead(Long taskId, String userEmail);

    /**
     * Retrieves unread message counts per task for a given user.
     *
     * @param email email of the user
     * @param role role of the user (CUSTOMER or HANDYMAN)
     * @return a map of taskId to number of unread messages
     */
    Map<Long, Long> getUnreadCounts(String email, Role role);
}
