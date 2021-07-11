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
import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.Task;
import com.javier.projectmanagement.services.ProjectService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  ProjectService projectService;

  @GetMapping()
  List<Project> getProjects() {
    return projectService.getProjects();
  }

  @GetMapping("/{projectID}")
  Project getProject(@PathVariable String projectID) {
    return projectService.getProject(projectID);
  }

  @GetMapping("/{projectID}/assignees")
  List<Employee> getAssignees(@PathVariable String projectID) {
    return projectService.getAssignees(projectID);
  }

  @GetMapping("/{projectID}/tasks")
  List<Task> getTasks(@PathVariable String projectID) {
    return projectService.getTasks(projectID);
  }

  @PostMapping
  boolean createProject(@RequestBody Project project) {
    return projectService.createProject(project);
  }

  @PutMapping("/{projectID}")
  boolean updateProject(@RequestBody Project project, @PathVariable String projectID) {
    return projectService.updateProject(project, projectID);
  }

  @DeleteMapping("/{projectID}")
  boolean deleteProject(@PathVariable String projectID) {
    return projectService.deleteProject(projectID);
  }
}
