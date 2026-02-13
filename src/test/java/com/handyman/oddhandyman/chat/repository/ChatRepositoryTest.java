package com.handyman.oddhandyman.chat.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User customer;
    private User handyman;
    private Task task;

    @BeforeEach
    void setUp() {
        // 1. Create and persist Users
        customer = new User();
        customer.setEmail("customer@test.com");
        customer.setName("John Customer");
        customer.setRole(Role.CUSTOMER);
        customer.setPassword("pass");
        entityManager.persist(customer);

        handyman = new User();
        handyman.setEmail("handy@test.com");
        handyman.setName("Bob Handyman");
        handyman.setRole(Role.HANDYMAN);
        handyman.setPassword("pass");
        entityManager.persist(handyman);

        // 2. Create and persist Task
        task = new Task();
        task.setTitle("Fix Leak");
        task.setCustomer(customer);
        task.setAssignedHandyman(handyman);
        task.setStatus(TaskStatus.ASSIGNED);
        entityManager.persist(task);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find unread messages for Customer sent by Handyman")
    void findUnreadForCustomer_ByHandymanSender() {
        // Given: Message from Handyman to Customer (unread by customer)
        ChatMessage msg = new ChatMessage();
        msg.setTask(task);
        msg.setSender(handyman);
        msg.setMessage("I am on my way");
        msg.setReadByCustomer(false);
        entityManager.persist(msg);
        entityManager.flush();

        // When
        List<ChatMessage> results = chatRepository
                .findByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse("customer@test.com", Role.HANDYMAN);

        // Then
        assertEquals(1, results.size());
        assertEquals("I am on my way", results.get(0).getMessage());
    }

    @Test
    @DisplayName("Should find unread messages for Handyman sent by Customer")
    void findUnreadForHandyman_ByCustomerSender() {
        // Given: Message from Customer to Handyman (unread by handyman)
        ChatMessage msg = new ChatMessage();
        msg.setTask(task);
        msg.setSender(customer);
        msg.setMessage("Gate is open");
        msg.setReadByHandyman(false);
        entityManager.persist(msg);
        entityManager.flush();

        // When
        List<ChatMessage> results = chatRepository
                .findByTaskAssignedHandymanEmailAndSenderRoleAndReadByHandymanFalse("handy@test.com", Role.CUSTOMER);

        // Then
        assertEquals(1, results.size());
        assertEquals("Gate is open", results.get(0).getMessage());
    }

    @Test
    @DisplayName("Should retrieve messages for task ordered by timestamp")
    void findByTask_OrderedByTimestamp() {
        // Given: Two messages with different timestamps (automatic via entity default)
        ChatMessage first = new ChatMessage();
        first.setTask(task);
        first.setSender(customer);
        first.setMessage("First");
        entityManager.persist(first);

        ChatMessage second = new ChatMessage();
        second.setTask(task);
        second.setSender(handyman);
        second.setMessage("Second");
        entityManager.persist(second);

        entityManager.flush();

        // When
        List<ChatMessage> results = chatRepository.findByTaskOrderByTimestampAsc(task);

        // Then
        assertEquals(2, results.size());
        assertEquals("First", results.get(0).getMessage());
        assertEquals("Second", results.get(1).getMessage());
    }
}
