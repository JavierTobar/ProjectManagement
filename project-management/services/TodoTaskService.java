package com.javier.projectmanagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.javier.projectmanagement.database.TodoTask;

@Service
public class TodoTaskService {

  @Autowired
  TodoTaskService todoTaskService;

  public List<TodoTask> getTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTasks::add);
    return todoTasks;
  }

  public List<TodoTask> getCompletedTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTask -> {
      if (todoTask.isCompleted())
        todoTasks.add(todoTask);
    });
    return todoTasks;
  }

  public List<TodoTask> getUncompletedTodoTasks() {
    List<TodoTask> todoTasks = new ArrayList<>();
    repositoryService.getTodoTaskRepository().findAll().forEach(todoTask -> {
      if (!todoTask.isCompleted())
        todoTasks.add(todoTask);
    });
    return todoTasks;
  }

  public TodoTask createTodoTask(@RequestBody TodoTask todoTask) {
    return repositoryService.getTodoTaskRepository().save(todoTask);
  }

  public boolean updateTodoTask(@RequestBody TodoTask todoTask, @PathVariable Integer todoTaskID) {
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

  public boolean deleteTodoTask(@PathVariable Integer todoTaskID) {
    Optional<TodoTask> todoTaskOptional =
        repositoryService.getTodoTaskRepository().findById(todoTaskID);
    if (!todoTaskOptional.isPresent())
      return false;

    repositoryService.getTodoTaskRepository().deleteById(todoTaskID);
    return true;
  }
}
