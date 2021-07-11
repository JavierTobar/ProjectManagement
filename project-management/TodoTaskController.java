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
import com.javier.projectmanagement.database.TodoTask;
import com.javier.projectmanagement.services.TodoTaskService;

@RestController
@RequestMapping("/todotasks")
public class TodoTaskController {

  @Autowired
  TodoTaskService todoTaskService;

  @GetMapping()
  List<TodoTask> getTodoTasks() {
    return todoTaskService.getTodoTasks();
  }

  @GetMapping("/completed")
  List<TodoTask> getCompletedTodoTasks() {
    return todoTaskService.getCompletedTodoTasks();
  }

  @GetMapping("/uncompleted")
  List<TodoTask> getUncompletedTodoTasks() {
    return todoTaskService.getUncompletedTodoTasks();
  }

  @PostMapping
  TodoTask createTodoTask(@RequestBody TodoTask todoTask) {
    return todoTaskService.createTodoTask(todoTask);
  }

  @PutMapping("/{todoTaskID}")
  boolean updateTodoTask(@RequestBody TodoTask todoTask, @PathVariable Integer todoTaskID) {
    return todoTaskService.updateTodoTask(todoTask, todoTaskID);
  }

  @DeleteMapping("/{todoTaskID}")
  boolean deleteTodoTask(@PathVariable Integer todoTaskID) {
    return todoTaskService.deleteTodoTask(todoTaskID);
  }
}
