package com.handyman.oddhandyman.task.service.impl;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.exception.UnacceptableOperationException;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private TaskServiceImpl taskService;

    private User mockCustomer;
    private User mockHandyman;
    private Task mockTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        mockCustomer = new User();
        mockCustomer.setId(1L);
        mockCustomer.setEmail("customer@test.com");
        mockCustomer.setRole(Role.CUSTOMER);

        mockHandyman = new User();
        mockHandyman.setId(2L);
        mockHandyman.setEmail("handy@test.com");
        mockHandyman.setRole(Role.HANDYMAN);

        mockTask = new Task();
        mockTask.setId(100L);
        mockTask.setTitle("Repair Sink");
        mockTask.setCustomer(mockCustomer);
        mockTask.setStatus(TaskStatus.PENDING);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Repair Sink");
        taskRequest.setBudget(200.0);
        taskRequest.setDeadline(LocalDateTime.now().plusDays(1));
    }

    @Nested
    @DisplayName("createTask() Logic")
    class CreateTaskTests {

        @Test
        @DisplayName("Positive: Should create task when user is a CUSTOMER")
        void createTask_Success() {
            when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(mockCustomer));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

            Task result = taskService.createTask(taskRequest, "customer@test.com");

            assertNotNull(result);
            assertEquals(TaskStatus.PENDING, result.getStatus());
            assertEquals(mockCustomer, result.getCustomer());
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Negative: Should throw exception if user is a HANDYMAN trying to create a task")
        void createTask_Handyman_Fails() {
            when(userRepository.findByEmail("handy@test.com")).thenReturn(Optional.of(mockHandyman));

            assertThrows(UnacceptableOperationException.class, () ->
                    taskService.createTask(taskRequest, "handy@test.com"));
        }
    }

    @Nested
    @DisplayName("getTasksForUser() Logic")
    class GetTasksTests {

        @Test
        @DisplayName("Positive: Should return only owned tasks for CUSTOMER")
        void getTasks_Customer() {
            when(taskRepository.findByCustomer(mockCustomer)).thenReturn(List.of(mockTask));

            List<TaskResponse> results = taskService.getTasksForUser(mockCustomer);

            assertEquals(1, results.size());
            verify(taskRepository, times(1)).findByCustomer(mockCustomer);
            verify(taskRepository, never()).findByStatus(any());
        }

        @Test
        @DisplayName("Positive: Should return available, assigned, and completed tasks for HANDYMAN")
        void getTasks_Handyman() {
            when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(mockTask));
            when(taskRepository.findByAssignedHandymanAndStatus(eq(mockHandyman), any()))
                    .thenReturn(List.of());

            List<TaskResponse> results = taskService.getTasksForUser(mockHandyman);

            assertNotNull(results);
            verify(taskRepository).findByStatus(TaskStatus.PENDING);
            verify(taskRepository, atLeastOnce()).findByAssignedHandymanAndStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("Task State Transitions")
    class StateTransitionTests {

        @Test
        @DisplayName("Positive: Should transition to ASSIGNED when handyman is assigned")
        void assignHandyman_Success() {
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(userRepository.findById(2L)).thenReturn(Optional.of(mockHandyman));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

            Task result = taskService.assignHandyman(100L, 2L);

            assertEquals(TaskStatus.ASSIGNED, result.getStatus());
            assertEquals(mockHandyman, result.getAssignedHandyman());
        }

        @Test
        @DisplayName("Positive: Should transition to COMPLETED when correct handyman completes it")
        void completeTask_Success() {
            mockTask.setAssignedHandyman(mockHandyman);
            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

            Task result = taskService.completeTask(100L, mockHandyman);

            assertEquals(TaskStatus.COMPLETED, result.getStatus());
        }

        @Test
        @DisplayName("Negative: Should throw exception if wrong handyman tries to complete task")
        void completeTask_WrongUser_Fails() {
            User wrongHandyman = new User();
            wrongHandyman.setId(99L);
            mockTask.setAssignedHandyman(mockHandyman);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(UnacceptableOperationException.class, () ->
                    taskService.completeTask(100L, wrongHandyman));
        }
    }
}
