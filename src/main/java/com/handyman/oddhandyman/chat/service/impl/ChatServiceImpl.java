package com.handyman.oddhandyman.chat.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.chat.dto.ChatMessageRequest;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.chat.repository.ChatRepository;
import com.handyman.oddhandyman.chat.service.ChatService;
import com.handyman.oddhandyman.exception.TaskNotFoundException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link ChatService} for handling messaging between customers and handymen.
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ChatServiceImpl(ChatRepository chatRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Sends a new chat message for a specific task.
     *
     * @param req the {@link ChatMessageRequest} containing taskId and message content
     * @param senderEmail email of the user sending the message
     * @return the saved {@link ChatMessage} entity
     */
    @Transactional
    @Override
    public ChatMessage sendMessage(ChatMessageRequest req, String senderEmail) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));

        ChatMessage message = new ChatMessage();
        message.setTask(task);
        message.setSender(sender);
        message.setMessage(req.getMessage());

        boolean senderIsCustomer = sender.getId().equals(task.getCustomer().getId());

        // If handyman is assigned, use assignedHandyman
        // User handyman = task.getAssignedHandyman();

        // Determine unread behaviour:
        if (senderIsCustomer) {
            // Customer sends → unread for handyman
            message.setReadByCustomer(true);
            message.setReadByHandyman(false);
        } else {
            // Handyman sends → unread for customer
            message.setReadByCustomer(false);
            message.setReadByHandyman(true);
        }

        return chatRepository.save(message);
    }


    /**
     * Retrieves all chat messages for a given task, ordered by timestamp ascending.
     *
     * @param taskId the ID of the task
     * @return list of {@link ChatMessage} for the task
     */
    @Override
    public List<ChatMessage> getMessagesForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));
        return chatRepository.findByTaskOrderByTimestampAsc(task);
    }

    /**
     * Marks messages as read for a specific task and user.
     * <p>
     * If the user is a customer, it marks all messages from the handyman as read.
     * If the user is a handyman, it marks all messages from the customer as read.
     *
     * @param taskId the ID of the task
     * @param userEmail email of the user marking messages as read
     */
    @Transactional
    @Override
    public void markMessagesAsRead(Long taskId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));

        boolean isCustomer = user.getId().equals(task.getCustomer().getId());

        List<ChatMessage> messages = chatRepository.findByTaskOrderByTimestampAsc(task);

        for (ChatMessage msg : messages) {
            if (isCustomer) {
                msg.setReadByCustomer(true);
            } else {
                msg.setReadByHandyman(true);
            }
        }

        chatRepository.saveAll(messages);
    }

    /**
     * Retrieves unread message counts per task for a given user.
     *
     * @param email email of the user
     * @param role role of the user (CUSTOMER or HANDYMAN)
     * @return a map of taskId to number of unread messages
     */
    @Override
    public Map<Long, Long> getUnreadCounts(String email, Role role) {
        Map<Long, Long> result = new HashMap<>();

        if (role == Role.CUSTOMER) {
            List<ChatMessage> messages =
                    chatRepository.findByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse(
                            email,
                            Role.HANDYMAN
                    );

            messages.forEach(msg ->
                    result.put(
                            msg.getTask().getId(),
                            result.getOrDefault(msg.getTask().getId(), 0L) + 1
                    )
            );

        } else if (role == Role.HANDYMAN) {
            List<ChatMessage> messages =
                    chatRepository.findByTaskAssignedHandymanEmailAndSenderRoleAndReadByHandymanFalse(
                            email,
                            Role.CUSTOMER
                    );

            messages.forEach(msg ->
                    result.put(
                            msg.getTask().getId(),
                            result.getOrDefault(msg.getTask().getId(), 0L) + 1
                    )
            );
        }

        return result;
    }
}
