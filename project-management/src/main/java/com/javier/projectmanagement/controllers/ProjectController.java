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
import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  RepositoryService repositoryService;

  // I want the following apis
  // , Delete, Update

  @GetMapping()
  List<Project> getProjects() {
    List<Project> projects = new ArrayList<>();
    repositoryService.getProjectRepository().findAll().forEach(project -> {
      project.updateCriticalPath();
      projects.add(project);
    });
    return projects;
  }

  @GetMapping("/{projectID}")
  Project getProject(@PathVariable String projectID) {
    return repositoryService.getProjectRepository().findById(projectID).orElseThrow();
  }

  @GetMapping("/{projectID}/assignees")
  List<Employee> getAssignees(@PathVariable String projectID) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(projectID);
    // if the project already exists, we return false
    if (!optionalProject.isPresent())
      return new ArrayList<>();
    List<Employee> assignees = new ArrayList<>();
    for (Employee e : optionalProject.get().getEmployees())
      assignees.add(e);
    return assignees;
  }

  @GetMapping("/{projectID}/tasks")
  List<Task> getTasks(@PathVariable String projectID) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(projectID);
    // if the project already exists, we return false
    if (!optionalProject.isPresent())
      return new ArrayList<>();
    List<Task> tasks = new ArrayList<>();
    for (Task t : optionalProject.get().getTasks())
      tasks.add(t);
    return tasks;
  }

  /**
   * Creates project
   * 
   * @param project
   * @return true if project got successfully created, else false
   */
  @PostMapping
  boolean createProject(@RequestBody Project project) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(project.getProject());
    // if the project already exists, we return false
    if (optionalProject.isPresent())
      return false;

    repositoryService.getProjectRepository().save(project);
    return true;
  }

  /**
   * Updates project
   * 
   * @param project
   * @param projectID
   * @return false if project didn't exist before, else updates project and return true
   */
  @PutMapping("/{projectID}")
  boolean updateProject(@RequestBody Project project, @PathVariable String projectID) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(projectID);
    // return false if project doesn't already exist or if you're trying to set the limit to
    // something below the current # of devs working
    if (!optionalProject.isPresent()
        || optionalProject.get().getEmployees().size() > project.getDevsLimit())
      return false;

    optionalProject.get().setDescription(project.getDescription());
    optionalProject.get().setStatus(project.getStatus());
    optionalProject.get().setDevsLimit(project.getDevsLimit());
    repositoryService.getProjectRepository().save(optionalProject.get());
    return true;
  }

  /**
   * Delete project if it exists. This also needs to delete all the tasks assigned to it. This is a
   * very destructive end point.
   * 
   * @param projectID
   * @return false if project isn't in DB, else deletes & true
   */
  @DeleteMapping("/delete/{projectID}")
  boolean deleteProject(@PathVariable String projectID) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(projectID);
    // return false if project doesn't exist
    if (!optionalProject.isPresent())
      return false;

    // Delete all the tasks in this project before deleting the project
    for (Task t : optionalProject.get().getTasks())
      repositoryService.getTaskRepository().deleteById(t.getTaskID());

    // Unassign employees
    for (Employee e : optionalProject.get().getEmployees()) {
      e.removeProject(optionalProject.get());
      repositoryService.getEmployeeRepository().save(e);
    }
    repositoryService.getProjectRepository().deleteById(projectID);
    return true;
  }


}
