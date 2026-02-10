package com.handyman.oddhandyman.task.repository;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Task} entities.
 * <p>
 * Provides custom query methods for retrieving tasks by customer, status, or assigned handyman.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Retrieves all tasks created by a specific customer.
     *
     * @param customer the customer who created the tasks
     * @return list of tasks created by the customer
     */
    List<Task> findByCustomer(User customer);

    /**
     * Retrieves all tasks with the given status.
     * <p>
     * Typically used by handymen to see open (PENDING) tasks available for bidding.
     *
     * @param status the task status to filter by
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Retrieves all tasks assigned to a specific handyman with a given status.
     * <p>
     * Useful for handymen to view their current assigned or completed tasks.
     *
     * @param handyman the handyman assigned to the tasks
     * @param status the status of tasks to filter by
     * @return list of tasks assigned to the handyman with the specified status
     */
    List<Task> findByAssignedHandymanAndStatus(User handyman, TaskStatus status);
}
