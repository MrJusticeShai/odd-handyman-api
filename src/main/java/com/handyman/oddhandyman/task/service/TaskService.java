package com.handyman.oddhandyman.task.service;

import com.handyman.oddhandyman.auth.entity.User;
import com.handyman.oddhandyman.task.dto.TaskRequest;
import com.handyman.oddhandyman.task.dto.TaskResponse;
import com.handyman.oddhandyman.task.entity.Task;
import java.util.List;

public interface TaskService {

    Task createTask(TaskRequest req, String customerEmail);

    List<TaskResponse> getTasksForUser(User user);

    Task getTask(Long id);

    Task assignHandyman(Long taskId, Long handymanId);

    Task completeTask(Long taskId, User handyman);

}
