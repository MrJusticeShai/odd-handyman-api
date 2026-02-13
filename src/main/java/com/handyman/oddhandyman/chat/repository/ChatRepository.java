package com.handyman.oddhandyman.chat.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link ChatMessage} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard JPA operations (save, findById, delete, etc.)
 * and declares custom query methods specific to chat messages.
 */
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Retrieves all chat messages associated with a specific task,
     * ordered by timestamp ascending.
     *
     * @param task the task whose chat messages should be retrieved
     * @return list of {@link ChatMessage} for the task, oldest first
     */
    List<ChatMessage> findByTaskOrderByTimestampAsc(Task task);

    /**
     * Retrieves unread messages sent by a handyman to the customer.
     *
     * @param email customer email
     * @param senderRole role of the sender (HANDYMAN)
     * @return list of {@link ChatMessage} unread by the customer
     */
    List<ChatMessage> findByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse(String email, Role senderRole);

    /**
     * Retrieves unread messages sent by a customer to the handyman.
     *
     * @param email handyman email
     * @param senderRole role of the sender (CUSTOMER)
     * @return list of {@link ChatMessage} unread by the handyman
     */
    List<ChatMessage> findByTaskAssignedHandymanEmailAndSenderRoleAndReadByHandymanFalse(String email, Role senderRole);
}
