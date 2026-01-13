package com.oddhandyman.task.service;

import com.oddhandyman.auth.entity.User;
import com.oddhandyman.task.dto.TaskRequest;
import com.oddhandyman.task.dto.TaskResponse;
import com.oddhandyman.task.entity.Task;
import java.util.List;

public interface TaskService {

    Task createTask(TaskRequest req, String customerEmail);

    List<TaskResponse> getTasksForUser(User user);

    Task getTask(Long id);

    Task assignHandyman(Long taskId, Long handymanId);

    Task completeTask(Long taskId, User handyman);

}
