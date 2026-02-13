package com.handyman.oddhandyman.chat.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.chat.dto.ChatMessageRequest;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.chat.repository.ChatRepository;
import com.handyman.oddhandyman.exception.TaskNotFoundException;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private ChatRepository chatRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ChatServiceImpl chatService;

    private User mockCustomer;
    private User mockHandyman;
    private Task mockTask;
    private ChatMessageRequest chatRequest;

    @BeforeEach
    void setUp() {
        mockCustomer = new User();
        mockCustomer.setId(1L);
        mockCustomer.setEmail("customer@test.com");

        mockHandyman = new User();
        mockHandyman.setId(2L);
        mockHandyman.setEmail("handy@test.com");

        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setCustomer(mockCustomer);
        mockTask.setAssignedHandyman(mockHandyman);

        chatRequest = new ChatMessageRequest();
        chatRequest.setTaskId(100L);
        chatRequest.setMessage("Test message content");
    }

    @Nested
    @DisplayName("sendMessage() Logic")
    class SendMessageTests {

        @Test
        @DisplayName("Positive: Customer sending message sets readByCustomer=true, readByHandyman=false")
        void sendMessage_FromCustomer() {
            when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(mockCustomer));
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(chatRepository.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

            ChatMessage result = chatService.sendMessage(chatRequest, "customer@test.com");

            assertTrue(result.isReadByCustomer());
            assertFalse(result.isReadByHandyman());
            assertEquals(mockCustomer, result.getSender());
        }

        @Test
        @DisplayName("Positive: Handyman sending message sets readByCustomer=false, readByHandyman=true")
        void sendMessage_FromHandyman() {
            when(userRepository.findByEmail("handy@test.com")).thenReturn(Optional.of(mockHandyman));
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(chatRepository.save(any(ChatMessage.class))).thenAnswer(i -> i.getArgument(0));

            ChatMessage result = chatService.sendMessage(chatRequest, "handy@test.com");

            assertFalse(result.isReadByCustomer());
            assertTrue(result.isReadByHandyman());
            assertEquals(mockHandyman, result.getSender());
        }
    }

    @Nested
    @DisplayName("markMessagesAsRead() Logic")
    class MarkReadTests {

        @Test
        @DisplayName("Positive: Marking as read for customer updates readByCustomer for all messages")
        void markRead_AsCustomer() {
            ChatMessage msg = new ChatMessage();
            msg.setReadByCustomer(false);

            when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(mockCustomer));
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(chatRepository.findByTaskOrderByTimestampAsc(mockTask)).thenReturn(List.of(msg));

            chatService.markMessagesAsRead(100L, "customer@test.com");

            assertTrue(msg.isReadByCustomer());
            verify(chatRepository).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("getUnreadCounts() Logic")
    class UnreadCountTests {

        @Test
        @DisplayName("Positive: Should aggregate unread messages per task for Customer")
        void getUnreadCounts_Customer() {
            ChatMessage msg = new ChatMessage();
            msg.setTask(mockTask);

            when(chatRepository.findByTaskCustomerEmailAndSenderRoleAndReadByCustomerFalse("customer@test.com", Role.HANDYMAN))
                    .thenReturn(Arrays.asList(msg, msg)); // 2 unread for task 100

            Map<Long, Long> counts = chatService.getUnreadCounts("customer@test.com", Role.CUSTOMER);

            assertEquals(1, counts.size());
            assertEquals(2L, counts.get(100L));
        }
    }

    @Nested
    @DisplayName("Exception Handling")
    class ExceptionTests {
        @Test
        @DisplayName("Negative: Throw TaskNotFoundException when taskId is invalid")
        void getMessages_InvalidTask_ThrowsException() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> chatService.getMessagesForTask(999L));
        }
    }
}
