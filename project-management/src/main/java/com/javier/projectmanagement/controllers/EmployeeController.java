package com.javier.projectmanagement.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.javier.projectmanagement.mapstruct.EmployeeGetDTO;
import com.javier.projectmanagement.mapstruct.EmployeePostDTO;
import com.javier.projectmanagement.mapstruct.MapStructMapper;


@RestController
@RequestMapping("/employees")
public class EmployeeController {


  @Autowired
  RepositoryService repositoryService;

  @Autowired
  MapStructMapper mapStructMapper;


  // EMPLOYEE PART

  @GetMapping()
  List<EmployeeGetDTO> getEmployees() {
    List<EmployeeGetDTO> employees = new ArrayList<>();
    repositoryService.getEmployeeRepository().findAll()
        .forEach(e -> employees.add(mapStructMapper.employeeToEmployeeGetDTO(e)));
    return employees;
  }

  @GetMapping("/{id}")
  ResponseEntity<EmployeeGetDTO> getEmployee(@PathVariable Integer id) {
    return new ResponseEntity<>(mapStructMapper.employeeToEmployeeGetDTO(
        repositoryService.getEmployeeRepository().findById(id).get()), HttpStatus.OK);

  }

  @PostMapping
  ResponseEntity<Void> createEmployee(@RequestBody EmployeePostDTO employee) {
    repositoryService.getEmployeeRepository()
        .save(mapStructMapper.employeePostDTOToEmployee(employee));

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * @param employee
   * @param employeeID
   * @return false if employee isn't in DB, else updates & true
   */
  @PutMapping("/{employeeID}")
  boolean updateEmployee(@RequestBody Employee employee, @PathVariable Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    if (!employeeOptional.isPresent())
      return false;

    employeeOptional.get().setName(employee.getName());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return true;
  }

  /**
   * Delete employee if it exists
   * 
   * @param employeeID
   * @return false if employee isn't in DB, else deletes & true
   */
  @DeleteMapping("/delete/{employeeID}")
  boolean deleteEmployee(@PathVariable Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    if (!employeeOptional.isPresent())
      return false;

    // in order to avoid ConcurrentModificationException
    List<Project> projectsToRemove = new ArrayList<>(employeeOptional.get().getProjects());
    // unassign from all projects before deleting
    for (Project p : projectsToRemove)
      employeeOptional.get().removeProject(p);

    repositoryService.getEmployeeRepository().deleteById(employeeID);
    return true;
  }

  // PROJECT PART

  /**
   * 
   * @param employeeID
   * @param projectID
   * @return false if employee or project doesn't exist, false if project full, else adds employee
   *         and return true
   */
  @PutMapping("/{employeeID}/assignproject/{projectID}")
  boolean assignProject(@PathVariable Integer employeeID, @PathVariable String projectID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    Optional<Project> projectOptional =
        repositoryService.getProjectRepository().findById(projectID);
    // Check is valid Employee & Valid Project & Project has space
    if (!employeeOptional.isPresent() || !projectOptional.isPresent()
        || projectOptional.get().getEmployees().size() >= projectOptional.get().getDevsLimit())
      return false;

    employeeOptional.get().addProject(projectOptional.get());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return true;
  }

  /**
   * If you unassign an employee from a project and they had tasks belonging to that project, these
   * tasks will now have no assignee
   * 
   * @param employeeID
   * @param projectID
   * @return
   */
  @PutMapping("/{employeeID}/unassignproject/{projectID}")
  boolean unassignProject(@PathVariable Integer employeeID, @PathVariable String projectID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    Optional<Project> projectOptional =
        repositoryService.getProjectRepository().findById(projectID);
    // Check is valid Employee & valid project
    if (!employeeOptional.isPresent() || !projectOptional.isPresent())
      return false;


    boolean succeeded = employeeOptional.get().removeProject(projectOptional.get());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return succeeded;
  }



  // TASK PART

  /**
   * @param employee
   * @param employeeID
   * @return false if employee isn't in DB, else updates & true
   */
  @GetMapping("/{employeeID}/tasks")
  Set<Task> getTasks(@PathVariable Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    if (!employeeOptional.isPresent())
      return new HashSet<Task>();


    return repositoryService.getEmployeeRepository().findById(employeeID).get().getTasks();
  }

  /**
   * Used to reassign or assign
   * 
   * @param taskID
   * @param employeeID
   * @return false if invalid employeeID or invalid taskID or employee doens't belong to project,
   *         else assigns the task and return true
   */
  @PutMapping("/{employeeID}/assigntask/{taskID}")
  boolean assignTask(@PathVariable String taskID, @PathVariable Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    Optional<Task> taskOptional = repositoryService.getTaskRepository().findById(taskID);

    // Check is valid Employee & valid taskID
    if (!employeeOptional.isPresent() || !taskOptional.isPresent())
      return false;

    // Need to check if employee belongs to the project of the given task
    if (!employeeOptional.get().containsProject(taskOptional.get().getProject_fk()))
      return false;

    employeeOptional.get().addTask(taskOptional.get());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return true;

  }

  /**
   * Unassign a task from employee. DOES NOT DELETE TASK.
   * 
   * @param taskID
   * @param employeeID
   * @return false if invalid employeeID or invalid taskID, true if task was assigned to given
   *         employee and is now removed
   */
  @PutMapping("/{employeeID}/unassigntask/{taskID}")
  boolean unassignTask(@PathVariable String taskID, @PathVariable Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    Optional<Task> taskOptional = repositoryService.getTaskRepository().findById(taskID);
    // Check is valid Employee & valid task
    if (!employeeOptional.isPresent() || !taskOptional.isPresent())
      return false;


    boolean succeeded = employeeOptional.get().removeTask(taskOptional.get());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return succeeded;
  }

  // END POINTS FOR HARD CODED DATA

  /**
   * Hard coded data to assign projects
   */
  @GetMapping("/assignProjects")
  void assignProjects() {
    // Add tasks to existing employees
    Employee e1 = repositoryService.getEmployeeRepository().findById(1).get();
    // Employee e2 = employeeRepository.findById(2).get();
    // Employee e3 = employeeRepository.findById(3).get();
    // Employee e4 = employeeRepository.findById(4).get();
    // Employee e5 = employeeRepository.findById(5).get();
    // Employee e6 = employeeRepository.findById(6).get();

    Project p1 = repositoryService.getProjectRepository().findById("DeepMind").get();
    // Project p2 = projectRepository.findById("Apollo").get();
    Project p3 = repositoryService.getProjectRepository().findById("Titan").get();
    Project p4 = repositoryService.getProjectRepository().findById("Atlantis").get();

    e1.addProject(p1);
    // e1.addProject(p4);
    // e2.addProject(p1);
    // e3.addProject(p3);
    // e4.addProject(p4);
    // e5.addProject(p4);
    // e6.addProject(p4);

    repositoryService.getEmployeeRepository().save(e1);
    // employeeRepository.save(e2);
    // employeeRepository.save(e3);
    // employeeRepository.save(e4);
    // employeeRepository.save(e5);
    // employeeRepository.save(e6);
  }

  /*
   * Hard coded data to assign tasks
   */
  @GetMapping("/assignTasks")
  void assignTasks() {
    // Add tasks to existing employees
    Employee e1 = repositoryService.getEmployeeRepository().findById(1).get();
    // Employee e2 = employeeRepository.findById(2).get();
    // Employee e4 = employeeRepository.findById(4).get();
    // Employee e5 = employeeRepository.findById(5).get();

    Task t1 = repositoryService.getTaskRepository().findById("Task_0001").get();
    // Task t2 = taskRepository.findById("Task_0002").get();
    // Task t6 = taskRepository.findById("Task_0006").get();
    // Task t7 = taskRepository.findById("Task_0007").get();

    e1.addTask(t1);
    // e2.addTask(t2);
    // e4.addTask(t6);
    // e5.addTask(t7);

    repositoryService.getEmployeeRepository().save(e1);
    // employeeRepository.save(e2);
    // employeeRepository.save(e4);
    // employeeRepository.save(e5);
  }
}
