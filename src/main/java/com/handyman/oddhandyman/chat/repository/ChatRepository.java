package com.handyman.oddhandyman.chat.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByTaskOrderByTimestampAsc(Task task);

    // For CUSTOMER: messages sent by HANDYMAN to this customer's tasks and unread
    Long countByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse(String email, Role senderRole);

    // For HANDYMAN: messages sent by CUSTOMER to this handyman's tasks and unread
    Long countByTaskAssignedHandymanEmailAndSenderRoleAndReadByHandymanFalse(String email, Role senderRole);

    // Optional: get counts per task (if you want a Map of taskId -> count)
    List<ChatMessage> findByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse(String email, Role senderRole);
    List<ChatMessage> findByTaskAssignedHandymanEmailAndSenderRoleAndReadByHandymanFalse(String email, Role senderRole);

}
