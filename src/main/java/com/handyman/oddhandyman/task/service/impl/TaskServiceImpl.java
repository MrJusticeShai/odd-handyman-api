package com.handyman.oddhandyman.task.service.impl;

import com.handyman.oddhandyman.auth.dto.UserResponse;
import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.repository.UserRepository;
import com.handyman.oddhandyman.exception.TaskNotFoundException;
import com.handyman.oddhandyman.exception.UnacceptableOperationException;
import com.handyman.oddhandyman.exception.UserNotFoundException;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import com.handyman.oddhandyman.task.repository.TaskRepository;
import com.handyman.oddhandyman.task.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link TaskService} for managing tasks.
 * <p>
 * Provides operations for creating tasks, assigning handymen, completing tasks,
 * and retrieving tasks for users (customers or handymen).
 * Handles role-based restrictions and enforces business rules.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new task for a customer identified by email.
     * <p>
     * Only users with role CUSTOMER can create tasks.
     * Sets initial task status to {@link TaskStatus#PENDING}.
     *
     * @param req           the task request payload containing title, description, address, budget, and deadline
     * @param customerEmail the email of the customer creating the task
     * @return the created {@link Task} entity
     * @throws UserNotFoundException          if no user with the provided email exists
     * @throws UnacceptableOperationException if the user is not a customer
     */
    @Transactional
    public Task createTask(TaskRequest req, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new UserNotFoundException("Customer Not Found"));

        if (!customer.getRole().name().equals("CUSTOMER")) {
            throw new UnacceptableOperationException("Only Customers Can create Tasks");
        }

        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setAddress(req.getAddress());
        t.setBudget(req.getBudget());
        t.setDeadline(req.getDeadline());
        t.setCustomer(customer);
        t.setStatus(TaskStatus.PENDING);

        return taskRepository.save(t);
    }

    /**
     * Retrieves tasks relevant to a specific user.
     * <p>
     * For customers: all tasks they created.
     * For handymen: open tasks (PENDING) and tasks assigned or completed for them.
     *
     * @param user the user whose tasks are being retrieved
     * @return list of {@link TaskResponse} DTOs
     */
    public List<TaskResponse> getTasksForUser(User user) {
        List<Task> tasks;
        if (user.getRole().name().equals("CUSTOMER")) {
            tasks = taskRepository.findByCustomer(user);
        } else if (user.getRole().name().equals("HANDYMAN")) {
            tasks = new ArrayList<>();
            tasks.addAll(taskRepository.findByStatus(TaskStatus.PENDING));
            tasks.addAll(taskRepository.findByAssignedHandymanAndStatus(user, TaskStatus.ASSIGNED));
            tasks.addAll(taskRepository.findByAssignedHandymanAndStatus(user, TaskStatus.COMPLETED));
        } else {
            tasks = List.of();
        }

        return tasks.stream().map(this::mapToDto).toList();
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the ID of the task
     * @return the {@link Task} entity
     * @throws TaskNotFoundException if the task is not found
     */
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));
    }

    /**
     * Assigns a handyman to a task and updates its status to {@link TaskStatus#ASSIGNED}.
     *
     * @param taskId     the ID of the task
     * @param handymanId the ID of the handyman to assign
     * @return the updated {@link Task} entity
     * @throws TaskNotFoundException if the task does not exist
     * @throws UserNotFoundException if the handyman does not exist
     */
    @Transactional
    public Task assignHandyman(Long taskId, Long handymanId) {
        Task task = getTask(taskId);
        User handyman = userRepository.findById(handymanId)
                .orElseThrow(() -> new UserNotFoundException("Handyman Not Found"));
        task.setAssignedHandyman(handyman);
        task.setStatus(TaskStatus.ASSIGNED);
        return taskRepository.save(task);
    }

    /**
     * Marks a task as completed by the assigned handyman.
     *
     * @param taskId   the ID of the task
     * @param handyman the handyman completing the task
     * @return the updated {@link Task} entity
     * @throws TaskNotFoundException          if the task does not exist
     * @throws UnacceptableOperationException if the provided handyman is not assigned to the task
     */
    @Transactional
    public Task completeTask(Long taskId, User handyman) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));

        if (!task.getAssignedHandyman().getId().equals(handyman.getId())) {
            throw new UnacceptableOperationException("Only Handyman Can Complete Task");
        }

        task.setStatus(TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }

    /**
     * Maps a {@link Task} entity to a {@link TaskResponse} DTO for API responses.
     *
     * @param task the task entity to map
     * @return the {@link TaskResponse} containing task details and associated users
     */
    private TaskResponse mapToDto(Task task) {
        TaskResponse dto = new TaskResponse();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setAddress(task.getAddress());
        dto.setBudget(task.getBudget());
        dto.setDeadline(task.getDeadline());
        dto.setStatus(task.getStatus());

        if (task.getCustomer() != null) {
            UserResponse c = new UserResponse();
            c.setId(task.getCustomer().getId());
            c.setName(task.getCustomer().getName());
            c.setEmail(task.getCustomer().getEmail());
            dto.setCustomer(c);
        }

        if (task.getAssignedHandyman() != null) {
            UserResponse h = new UserResponse();
            h.setId(task.getAssignedHandyman().getId());
            h.setName(task.getAssignedHandyman().getName());
            h.setEmail(task.getAssignedHandyman().getEmail());
            dto.setAssignedHandyman(h);
        }

        return dto;
    }
}
