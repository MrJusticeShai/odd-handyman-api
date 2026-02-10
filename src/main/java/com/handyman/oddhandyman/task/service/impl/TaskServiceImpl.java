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

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

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

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task Not Found"));
    }

    @Transactional
    public Task assignHandyman(Long taskId, Long handymanId) {
        Task task = getTask(taskId);
        User handyman = userRepository.findById(handymanId)
                .orElseThrow(() -> new UserNotFoundException("Handyman Not Found"));
        task.setAssignedHandyman(handyman);
        task.setStatus(TaskStatus.ASSIGNED);
        return taskRepository.save(task);
    }

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
