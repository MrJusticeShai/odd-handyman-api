package com.handyman.oddhandyman.task.repository;

import com.handyman.oddhandyman.auth.entity.Role;
import com.handyman.oddhandyman.auth.entity.User;
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
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User customer;
    private User handyman;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setEmail("client@test.com");
        customer.setName("John Client");
        customer.setRole(Role.CUSTOMER);
        customer.setPassword("hashed_pass");
        entityManager.persist(customer);

        handyman = new User();
        handyman.setEmail("handy@test.com");
        handyman.setName("Bob Fixer");
        handyman.setRole(Role.HANDYMAN);
        handyman.setPassword("hashed_pass");
        entityManager.persist(handyman);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all tasks created by a specific customer")
    void findByCustomer_ReturnsTasks() {
        Task task = new Task();
        task.setTitle("Fix Window");
        task.setCustomer(customer);
        task.setStatus(TaskStatus.PENDING);
        entityManager.persist(task);
        entityManager.flush();

        List<Task> results = taskRepository.findByCustomer(customer);

        assertEquals(1, results.size());
        assertEquals("Fix Window", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find all tasks with PENDING status")
    void findByStatus_ReturnsPendingTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.PENDING);
        entityManager.persist(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.ASSIGNED);
        entityManager.persist(task2);

        entityManager.flush();

        List<Task> results = taskRepository.findByStatus(TaskStatus.PENDING);

        assertEquals(1, results.size());
        assertEquals(TaskStatus.PENDING, results.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find tasks by assigned handyman and specific status")
    void findByAssignedHandymanAndStatus_ReturnsMatchingTasks() {
        Task task = new Task();
        task.setTitle("Assigned Task");
        task.setAssignedHandyman(handyman);
        task.setStatus(TaskStatus.ASSIGNED);
        entityManager.persist(task);
        entityManager.flush();

        List<Task> results = taskRepository.findByAssignedHandymanAndStatus(handyman, TaskStatus.ASSIGNED);

        assertEquals(1, results.size());
        assertEquals(handyman.getEmail(), results.get(0).getAssignedHandyman().getEmail());
    }

    @Test
    @DisplayName("Should return empty list if no tasks match status")
    void findByStatus_NoMatch_ReturnsEmpty() {
        List<Task> results = taskRepository.findByStatus(TaskStatus.COMPLETED);
        assertTrue(results.isEmpty());
    }
}
