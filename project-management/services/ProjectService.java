package com.javier.projectmanagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;

@Service
public class ProjectService {

  @Autowired
  RepositoryService repositoryService;

  public List<Project> getProjects() {
    List<Project> projects = new ArrayList<>();
    repositoryService.getProjectRepository().findAll().forEach(project -> {
      project.updateCriticalPath();
      projects.add(project);
    });
    return projects;
  }

  public Project getProject(@PathVariable String projectID) {
    return repositoryService.getProjectRepository().findById(projectID).orElseThrow();
  }

  public List<Employee> getAssignees(@PathVariable String projectID) {
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

  public List<Task> getTasks(@PathVariable String projectID) {
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

  public boolean createProject(@RequestBody Project project) {
    Optional<Project> optionalProject =
        repositoryService.getProjectRepository().findById(project.getProject());
    // if the project already exists, we return false
    if (optionalProject.isPresent())
      return false;

    repositoryService.getProjectRepository().save(project);
    return true;
  }

  public boolean updateProject(@RequestBody Project project, @PathVariable String projectID) {
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

  public boolean deleteProject(@PathVariable String projectID) {
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
