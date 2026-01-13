package com.oddhandyman.task.repository;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.task.entity.Task;
import com.oddhandyman.task.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // For customers: all tasks they created
    List<Task> findByCustomer(User customer);

    // Open tasks (PENDING) for handymen
    List<Task> findByStatus(TaskStatus status);

    // Changed status tasks assigned to a handyman
    List<Task> findByAssignedHandymanAndStatus(User handyman, TaskStatus status);
}
