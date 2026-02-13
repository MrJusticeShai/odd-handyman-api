package com.handyman.oddhandyman.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    private Task fullTask;
    private User mockCustomer;
    private User mockHandyman;
    private final String CUSTOMER_EMAIL = "customer@example.com";
    private final String HANDYMAN_EMAIL = "handyman@example.com";
    private LocalDateTime fixedDeadline;

    @BeforeEach
    void setUp() {
        fixedDeadline = LocalDateTime.of(2026, 2, 15, 17, 0);

        mockCustomer = new User();
        mockCustomer.setId(1L);
        mockCustomer.setEmail(CUSTOMER_EMAIL);
        mockCustomer.setName("John Customer");
        mockCustomer.setRole(Role.CUSTOMER);
        mockCustomer.setVerified(true);

        mockHandyman = new User();
        mockHandyman.setId(2L);
        mockHandyman.setEmail(HANDYMAN_EMAIL);
        mockHandyman.setName("Bob Handyman");
        mockHandyman.setRole(Role.HANDYMAN);

        fullTask = new Task();
        fullTask.setId(100L);
        fullTask.setTitle("Fix leaking faucet");
        fullTask.setDescription("The kitchen faucet is leaking and needs repair");
        fullTask.setAddress("123 Main Street, Naboomspruite");
        fullTask.setBudget(150.0);
        fullTask.setDeadline(fixedDeadline);
        fullTask.setStatus(TaskStatus.PENDING);
        fullTask.setCustomer(mockCustomer);
    }

    @Nested
    @DisplayName("POST /api/tasks - Create Task")
    class CreateTaskTests {

        @Test
        @WithMockUser(username = CUSTOMER_EMAIL)
        @DisplayName("Positive: Should create task and return all fields")
        void createTask_Success() throws Exception {
            TaskRequest req = new TaskRequest();
            req.setTitle("Fix leaking faucet");
            req.setDescription("The kitchen faucet is leaking");
            req.setAddress("123 Main Street");
            req.setBudget(150.0);
            req.setDeadline(fixedDeadline);

            when(taskService.createTask(any(TaskRequest.class), eq(CUSTOMER_EMAIL))).thenReturn(fullTask);

            mockMvc.perform(post("/api/tasks")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.title").value("Fix leaking faucet"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.customer.email").value(CUSTOMER_EMAIL));
        }

        @Test
        @WithMockUser(username = CUSTOMER_EMAIL)
        @DisplayName("Negative: Should return 400 when budget is null")
        void createTask_InvalidValidation_Fails() throws Exception {
            TaskRequest invalidReq = new TaskRequest(); // Missing required @NotNull budget
            invalidReq.setTitle("Test");

            mockMvc.perform(post("/api/tasks")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidReq)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/tasks - List Tasks")
    class GetTasksTests {

        @Test
        @WithMockUser(username = CUSTOMER_EMAIL)
        @DisplayName("Positive: Should resolve user and return task list")
        void getTasks_Success() throws Exception {
            TaskResponse res = new TaskResponse();
            res.setId(100L);
            res.setTitle("Fix leaking faucet");

            when(userService.findByEmail(CUSTOMER_EMAIL)).thenReturn(mockCustomer);
            when(taskService.getTasksForUser(mockCustomer)).thenReturn(Collections.singletonList(res));

            mockMvc.perform(get("/api/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(100L))
                    .andExpect(jsonPath("$[0].title").value("Fix leaking faucet"));
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{id}/complete")
    class CompleteTaskTests {

        @Test
        @WithMockUser(username = HANDYMAN_EMAIL)
        @DisplayName("Positive: Should complete task and return status COMPLETED")
        void completeTask_Success() throws Exception {
            fullTask.setStatus(TaskStatus.COMPLETED);
            fullTask.setAssignedHandyman(mockHandyman);

            when(userService.findByEmail(HANDYMAN_EMAIL)).thenReturn(mockHandyman);
            when(taskService.completeTask(eq(100L), eq(mockHandyman))).thenReturn(fullTask);

            mockMvc.perform(put("/api/tasks/100/complete").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.assignedHandyman.email").value(HANDYMAN_EMAIL));
        }

        @Test
        @DisplayName("Negative: Should return 401 without authentication")
        void completeTask_Unauthorized_Fails() throws Exception {
            mockMvc.perform(put("/api/tasks/100/complete").with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{id}/assign/{handymanId}")
    class AssignTests {

        @Test
        @WithMockUser
        @DisplayName("Positive: Should assign handyman and return updated task")
        void assignHandyman_Success() throws Exception {
            fullTask.setStatus(TaskStatus.ASSIGNED);
            fullTask.setAssignedHandyman(mockHandyman);

            when(taskService.assignHandyman(100L, 2L)).thenReturn(fullTask);

            mockMvc.perform(put("/api/tasks/100/assign/2").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ASSIGNED"))
                    .andExpect(jsonPath("$.assignedHandyman.id").value(2L));
        }
    }
}
