package com.javier.projectmanagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.ProjectStatus;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;

@Service
public class TaskService {

  @Autowired
  RepositoryService repositoryService;

  public List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    repositoryService.getTaskRepository().findAll().forEach(tasks::add);
    return tasks;
  }

  public Task getTask(@PathVariable String id) {
    return repositoryService.getTaskRepository().findById(id).orElseThrow();
  }

  public boolean createTask(@RequestBody Task task) {
    String projectID = task.getProject_fk();
    Optional<Project> projectOptional =
        repositoryService.getProjectRepository().findById(projectID);
    if (!projectOptional.isPresent() || projectOptional.get().getStatus() == ProjectStatus.DONE)
      return false;

    // check if predecessor == successor, in which case fail the creation
    // make sure not to simply compare "" with ""
    if (task.getPredesessorTaskID() != null && !task.getPredesessorTaskID().isBlank()
        && task.getPredesessorTaskID() == task.getSuccessorTaskID())
      return false;

    task.updateDurationAndCriticalCost();
    projectOptional.get().addTask(task);

    // projectOptional.get().updateCriticalPath();
    // repositoryService.getProjectRepository().save(projectOptional.get());

    String taskID = repositoryService.getTaskRepository().save(task).getTaskID();
    // check if predesessor is valid
    if (task.getPredesessorTaskID() != null && !task.getPredesessorTaskID().isBlank()) {
      if (!repositoryService.getTaskRepository().findById(task.getPredesessorTaskID())
          .isPresent()) {
        repositoryService.getTaskRepository().deleteById(taskID);
        return false;
      }
      // update predesessor as well
      Task predecessor =
          repositoryService.getTaskRepository().findById(task.getPredesessorTaskID()).get();
      predecessor.setSuccessorTaskID(taskID);
      // if predecessor was current taskID, just reset it to blank
      // if (predecessor.getPredesessorTaskID() == task.getTaskID())
      // predecessor.setPredesessorTaskID("");

      repositoryService.getTaskRepository().save(predecessor);
    }

    // check if successor is valid
    if (task.getSuccessorTaskID() != null && !task.getSuccessorTaskID().isBlank()) {
      if (!repositoryService.getTaskRepository().findById(task.getSuccessorTaskID()).isPresent()) {
        repositoryService.getTaskRepository().deleteById(taskID);
        return false;
      }

      // update successor as well
      Task successor =
          repositoryService.getTaskRepository().findById(task.getSuccessorTaskID()).get();
      successor.setPredesessorTaskID(taskID);
      // if successor was current taskID, just reset it to blank
      // if (successor.getSuccessorTaskID() == task.getTaskID())
      // successor.setSuccessorTaskID("");
      repositoryService.getTaskRepository().save(successor);
    }
    return true;
  }

  public boolean updateTask(@RequestBody Task task, @PathVariable String taskID) {
    Optional<Task> taskOptional = repositoryService.getTaskRepository().findById(taskID);
    if (!taskOptional.isPresent())
      return false;

    task.updateDurationAndCriticalCost();
    task.setTaskID(taskID);
    repositoryService.getTaskRepository().save(task);
    // safe to get() because task will always have a project assigned to it
    Project project = repositoryService.getProjectRepository().findById(task.getProject_fk()).get();

    project.updateCriticalPath();
    repositoryService.getProjectRepository().save(project);
    return true;
  }

  public boolean deleteTask(@PathVariable String taskID) {
    Optional<Task> taskOptional = repositoryService.getTaskRepository().findById(taskID);
    Optional<Project> projectOptional =
        repositoryService.getProjectRepository().findById(taskOptional.get().getProject_fk());
    if (!taskOptional.isPresent())
      return false;

    // need to delete its reference from successors/predecessors
    if (taskOptional.get().getSuccessorTaskID() != null
        && !taskOptional.get().getSuccessorTaskID().isBlank()) {
      Task successor = repositoryService.getTaskRepository()
          .findById(taskOptional.get().getSuccessorTaskID()).get();
      successor.setPredesessorTaskID(null);
      repositoryService.getTaskRepository().save(successor);
    }

    // need to delete its reference from successors/predecessors
    if (taskOptional.get().getPredesessorTaskID() != null
        && !taskOptional.get().getSuccessorTaskID().isBlank()) {
      Task predesessor = repositoryService.getTaskRepository()
          .findById(taskOptional.get().getPredesessorTaskID()).get();
      predesessor.setSuccessorTaskID(null);
      repositoryService.getTaskRepository().save(predesessor);
    }

    repositoryService.getTaskRepository().deleteById(taskID);
    projectOptional.get().updateCriticalPath();
    repositoryService.getProjectRepository().save(projectOptional.get());
    return true;
  }
}
