package com.handyman.oddhandyman.task.service;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import java.util.List;

/**
 * Service interface defining business operations for managing tasks.
 * <p>
 * Handles creation, assignment, completion, and retrieval of tasks.
 */
public interface TaskService {

    /**
     * Creates a new task for a customer identified by their email.
     *
     * @param req           the task request payload containing title, description, address, budget, and deadline
     * @param customerEmail the email of the customer creating the task
     * @return the created {@link Task} entity
     */
    Task createTask(TaskRequest req, String customerEmail);

    /**
     * Retrieves a list of tasks relevant to a specific user.
     * <p>
     * For customers: returns tasks they created.
     * For handymen: returns tasks assigned to them or available to bid on.
     *
     * @param user the user whose tasks are being retrieved
     * @return list of {@link TaskResponse} DTOs
     */
    List<TaskResponse> getTasksForUser(User user);

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the ID of the task
     * @return the {@link Task} entity
     */
    Task getTask(Long id);

    /**
     * Assigns a handyman to a task.
     * <p>
     * Typically used when a bid is accepted or manually assigning a handyman.
     *
     * @param taskId     the ID of the task to assign
     * @param handymanId the ID of the handyman being assigned
     * @return the updated {@link Task} entity with assigned handyman
     */
    Task assignHandyman(Long taskId, Long handymanId);

    /**
     * Marks a task as completed by the handyman.
     * <p>
     * Updates the task status to COMPLETED status.
     *
     * @param taskId   the ID of the task to mark complete
     * @param handyman the handyman completing the task
     * @return the updated {@link Task} entity
     */
    Task completeTask(Long taskId, User handyman);

}
