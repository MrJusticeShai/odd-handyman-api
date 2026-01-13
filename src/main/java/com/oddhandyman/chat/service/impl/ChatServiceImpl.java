package com.oddhandyman.chat.service.impl;

import com.oddhandyman.auth.entity.Role;
import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.repository.UserRepository;
import com.oddhandyman.chat.dto.ChatMessageRequest;
import com.oddhandyman.chat.entity.ChatMessage;
import com.oddhandyman.chat.repository.ChatRepository;
import com.oddhandyman.chat.service.ChatService;
import com.oddhandyman.task.entity.Task;
import com.oddhandyman.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public ChatMessage sendMessage(ChatMessageRequest req, String senderEmail) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

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
            // Sender is handyman or bidder
            message.setReadByCustomer(false);
            message.setReadByHandyman(true);
        }

        return chatRepository.save(message);
    }


    public List<ChatMessage> getMessagesForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return chatRepository.findByTaskOrderByTimestampAsc(task);
    }

    @Transactional
    public void markMessagesAsRead(Long taskId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

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
