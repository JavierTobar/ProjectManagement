package com.javier.projectmanagement.controllers;

import java.util.List;
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
import com.javier.projectmanagement.database.Task;
import com.javier.projectmanagement.mapstruct.EmployeeGetDTO;
import com.javier.projectmanagement.mapstruct.EmployeePostDTO;
import com.javier.projectmanagement.services.EmployeeService;


@RestController
@RequestMapping("/employees")
public class EmployeeController {
  @Autowired
  EmployeeService employeeService;

  // EMPLOYEE PART
  @GetMapping()
  List<EmployeeGetDTO> getEmployees() {
    return employeeService.getEmployees();
  }

  @GetMapping("/{id}")
  ResponseEntity<EmployeeGetDTO> getEmployee(@PathVariable Integer id) {
    return new ResponseEntity<>(employeeService.getEmployee(id), HttpStatus.OK);
  }

  @PostMapping
  ResponseEntity<Void> createEmployee(@RequestBody EmployeePostDTO employee) {
    employeeService.createEmployee(employee);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping("/{employeeID}")
  boolean updateEmployee(@RequestBody Employee employee, @PathVariable Integer employeeID) {
    return employeeService.updateEmployee(employee, employeeID);
  }

  @DeleteMapping("/{employeeID}")
  boolean deleteEmployee(@PathVariable Integer employeeID) {
    return employeeService.deleteEmployee(employeeID);
  }

  // PROJECT PART
  @PutMapping("/{employeeID}/assignproject/{projectID}")
  boolean assignProject(@PathVariable Integer employeeID, @PathVariable String projectID) {
    return employeeService.assignProject(employeeID, projectID);
  }

  @PutMapping("/{employeeID}/unassignproject/{projectID}")
  boolean unassignProject(@PathVariable Integer employeeID, @PathVariable String projectID) {
    return employeeService.unassignProject(employeeID, projectID);
  }

  // TASK PART
  @GetMapping("/{employeeID}/tasks")
  Set<Task> getTasks(@PathVariable Integer employeeID) {
    return employeeService.getTasks(employeeID);
  }

  @PutMapping("/{employeeID}/assigntask/{taskID}")
  boolean assignTask(@PathVariable String taskID, @PathVariable Integer employeeID) {
    return employeeService.assignTask(taskID, employeeID);
  }

  @PutMapping("/{employeeID}/unassigntask/{taskID}")
  boolean unassignTask(@PathVariable String taskID, @PathVariable Integer employeeID) {
    return employeeService.unassignTask(taskID, employeeID);
  }
}
