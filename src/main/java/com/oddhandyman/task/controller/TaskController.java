package com.oddhandyman.task.controller;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.auth.service.UserService;
import com.oddhandyman.task.dto.TaskRequest;
import com.oddhandyman.task.dto.TaskResponse;
import com.oddhandyman.task.entity.Task;
import com.oddhandyman.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;

    }

    @Operation(summary = "Create a new task", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest req, Authentication auth) {
        Task t = taskService.createTask(req, auth.getName());
        return ResponseEntity.ok(t);
    }

    @Operation(summary = "List all tasks", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<TaskResponse> getTasks(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return taskService.getTasksForUser(user);
    }

    @Operation(summary = "Get task by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(summary = "Assign handyman to task", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/assign/{handymanId}")
    public ResponseEntity<Task> assignHandyman(@PathVariable Long id, @PathVariable Long handymanId) {
        return ResponseEntity.ok(taskService.assignHandyman(id, handymanId));
    }

    @Operation(summary = "Mark task as completed", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String email = authentication.getName();
        User handyman = userService.findByEmail(email);

        Task completed = taskService.completeTask(id, handyman);
        return ResponseEntity.ok(completed);
    }
}
