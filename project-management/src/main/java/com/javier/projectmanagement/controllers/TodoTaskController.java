package com.javier.projectmanagement.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.TodoTask;

@RestController
@RequestMapping("/todotasks")
public class TodoTaskController {

  @Autowired
  RepositoryService repositoryService;

  // We have getAll(), create(), update(), delete()


  @GetMapping()
  List<TodoTask> getTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTasks::add);
    return todoTasks;
  }

  @GetMapping("/completed")
  List<TodoTask> getCompletedTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTask -> {
      if (todoTask.isCompleted())
        todoTasks.add(todoTask);
    });
    return todoTasks;
  }

  @GetMapping("/uncompleted")
  List<TodoTask> getUncompletedTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTask -> {
      if (!todoTask.isCompleted())
        todoTasks.add(todoTask);
    });
    return todoTasks;
  }



  @PostMapping
  TodoTask createTodoTask(@RequestBody TodoTask todoTask) {
    return repositoryService.getTodoTaskRepository().save(todoTask);
  }

  @PutMapping("/{todoTaskID}")
  boolean updateTodoTask(@RequestBody TodoTask todoTask, @PathVariable Integer todoTaskID) {
    Optional<TodoTask> todoTaskOptional =
        repositoryService.getTodoTaskRepository().findById(todoTaskID);
    if (!todoTaskOptional.isPresent())
      return false;

    // manually retrieve fields rather than saving the object at once (lazy DTO attempt)
    todoTaskOptional.get().setDueDate(todoTask.getDueDate());
    todoTaskOptional.get().setDescription(todoTask.getDescription());
    todoTaskOptional.get().setTodoTaskPriority(todoTask.getTodoTaskPriority());
    todoTaskOptional.get().setCompleted(todoTask.isCompleted());

    repositoryService.getTodoTaskRepository().save(todoTaskOptional.get());
    return true;
  }

  @DeleteMapping("/delete/{todoTaskID}")
  boolean deleteTodoTask(@PathVariable Integer todoTaskID) {
    Optional<TodoTask> todoTaskOptional =
        repositoryService.getTodoTaskRepository().findById(todoTaskID);
    if (!todoTaskOptional.isPresent())
      return false;

    repositoryService.getTodoTaskRepository().deleteById(todoTaskID);
    return true;
  }

}
