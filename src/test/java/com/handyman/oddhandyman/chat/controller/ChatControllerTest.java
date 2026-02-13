package com.handyman.oddhandyman.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.chat.dto.ChatMessageRequest;
import com.handyman.oddhandyman.chat.entity.ChatMessage;
import com.handyman.oddhandyman.chat.service.ChatService;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    private ChatMessage fullMockMessage;
    private Task mockTask;
    private User mockSender;
    private final String TEST_EMAIL = "customer@example.com";

    @BeforeEach
    void setUp() {
        // Initialize Sender
        mockSender = new User();
        mockSender.setId(1L);
        mockSender.setEmail(TEST_EMAIL);
        mockSender.setName("John Customer");
        mockSender.setRole(Role.CUSTOMER);

        // Initialize Task
        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setTitle("Fix Sink");
        mockTask.setDescription("Leaking pipe in kitchen");
        mockTask.setStatus(TaskStatus.PENDING);
        mockTask.setCustomer(mockSender);

        // Initialize ChatMessage with full object graph
        fullMockMessage = new ChatMessage();
        fullMockMessage.setId(500L);
        fullMockMessage.setTask(mockTask);
        fullMockMessage.setSender(mockSender);
        fullMockMessage.setMessage("Hello, is this still available?");
        fullMockMessage.setTimestamp(LocalDateTime.of(2026, 2, 10, 14, 30));
        fullMockMessage.setReadByCustomer(true);
        fullMockMessage.setReadByHandyman(false);
    }

    @Nested
    @DisplayName("POST /api/chat - Send Message")
    class SendMessageTests {

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Positive: Should send message and compare full object graph")
        void sendMessage_Success() throws Exception {
            ChatMessageRequest req = new ChatMessageRequest();
            req.setTaskId(100L);
            req.setMessage("Hello, is this still available?");

            when(chatService.sendMessage(any(ChatMessageRequest.class), eq(TEST_EMAIL)))
                    .thenReturn(fullMockMessage);

            mockMvc.perform(post("/api/chat")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    // Core Fields
                    .andExpect(jsonPath("$.id").value(500L))
                    .andExpect(jsonPath("$.message").value("Hello, is this still available?"))
                    // Nested Task Verification
                    .andExpect(jsonPath("$.task.id").value(100L))
                    .andExpect(jsonPath("$.task.status").value("PENDING"))
                    // Nested Sender Verification
                    .andExpect(jsonPath("$.sender.email").value(TEST_EMAIL))
                    .andExpect(jsonPath("$.sender.role").value("CUSTOMER"))
                    // Read Status
                    .andExpect(jsonPath("$.readByCustomer").value(true))
                    .andExpect(jsonPath("$.readByHandyman").value(false));
        }

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Negative: 400 Bad Request on empty message content")
        void sendMessage_EmptyContent() throws Exception {
            ChatMessageRequest req = new ChatMessageRequest();
            req.setTaskId(100L);
            mockMvc.perform(post("/api/chat")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/chat/unread-count")
    class UnreadCountTests {

        @Test
        @DisplayName("Positive: Should return map and handle role enums")
        void getUnreadCounts_Success() throws Exception {
            Map<Long, Long> counts = Map.of(100L, 2L);
            when(chatService.getUnreadCounts(TEST_EMAIL, Role.CUSTOMER)).thenReturn(counts);

            mockMvc.perform(get("/api/chat/unread-count")
                            .param("email", TEST_EMAIL)
                            .param("role", "CUSTOMER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.100").value(2));
        }

        @Test
        @DisplayName("Negative: Should return 400 for unsupported Role (ADMIN)")
        void getUnreadCounts_ForbiddenRole() throws Exception {
            mockMvc.perform(get("/api/chat/unread-count")
                            .param("email", TEST_EMAIL)
                            .param("role", "ADMIN"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/chat/mark-read/{taskId}")
    class MarkReadTests {

        @Test
        @WithMockUser(username = TEST_EMAIL)
        @DisplayName("Positive: Should call service with principal email")
        void markRead_Success() throws Exception {
            mockMvc.perform(post("/api/chat/mark-read/100").with(csrf()))
                    .andExpect(status().isOk());

            verify(chatService).markMessagesAsRead(100L, TEST_EMAIL);
        }
    }
}
