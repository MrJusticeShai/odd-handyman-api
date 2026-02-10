package com.handyman.oddhandyman.task.controller;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.auth.service.UserService;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import com.handyman.oddhandyman.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tasks in the Handyman system.
 * <p>
 * Provides endpoints for creating tasks, listing tasks for a user,
 * retrieving tasks by ID, assigning handymen, and marking tasks as completed.
 * Security is enforced via JWT Bearer tokens.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;

    }

    @Operation(summary = "Create a new task",
            description = "Create New Task",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest req, Authentication auth) {
        Task t = taskService.createTask(req, auth.getName());
        return ResponseEntity.ok(t);
    }

    @Operation(summary = "List all tasks",
            description =  "Retrieves all tasks relevant to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<TaskResponse> getTasks(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return taskService.getTasksForUser(user);
    }

    @Operation(summary = "Get task by ID",
            description = "Retrieves a task by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(summary = "Assign handyman to task",
            description = "Assigns a handyman to a task and updates its status to ASSIGNED.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/assign/{handymanId}")
    public ResponseEntity<Task> assignHandyman(@PathVariable Long id, @PathVariable Long handymanId) {
        return ResponseEntity.ok(taskService.assignHandyman(id, handymanId));
    }

    @Operation(summary = "Mark task as completed",
            description = "Marks a task as completed by the assigned handyman.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String email = authentication.getName();
        User handyman = userService.findByEmail(email);

        Task completed = taskService.completeTask(id, handyman);
        return ResponseEntity.ok(completed);
    }
}
