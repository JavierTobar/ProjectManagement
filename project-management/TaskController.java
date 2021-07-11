package com.javier.projectmanagement.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javier.projectmanagement.database.Task;
import com.javier.projectmanagement.services.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  TaskService taskService;

  @GetMapping()
  List<Task> getTasks() {
    return taskService.getTasks();
  }

  @GetMapping("/{id}")
  Task getTask(@PathVariable String id) {
    return taskService.getTask(id);
  }

  @PostMapping
  boolean createTask(@RequestBody Task task) {
    return taskService.createTask(task);
  }

  @PutMapping("/{taskID}")
  boolean updateTask(@RequestBody Task task, @PathVariable String taskID) {
    return taskService.updateTask(task, taskID);
  }

  @DeleteMapping("/{taskID}")
  boolean deleteTask(@PathVariable String taskID) {
    return taskService.deleteTask(taskID);
  }
}
