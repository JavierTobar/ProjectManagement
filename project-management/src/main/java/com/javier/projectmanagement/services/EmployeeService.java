package com.javier.projectmanagement.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;
import com.javier.projectmanagement.mapstruct.EmployeeGetDTO;
import com.javier.projectmanagement.mapstruct.EmployeePostDTO;
import com.javier.projectmanagement.mapstruct.MapStructMapper;

@Service
public class EmployeeService {
  @Autowired
  RepositoryService repositoryService;

  @Autowired
  MapStructMapper mapStructMapper;

  public List<EmployeeGetDTO> getEmployees() {
    List<EmployeeGetDTO> employees = new ArrayList<>();
    repositoryService.getEmployeeRepository().findAll()
        .forEach(e -> employees.add(mapStructMapper.employeeToEmployeeGetDTO(e)));
    return employees;
  }

  public EmployeeGetDTO getEmployee(@PathVariable Integer id) {
    return repositoryService.getEmployeeRepository().findById(id).get();
  }

  public void createEmployee(EmployeePostDTO employee) {
    repositoryService.getEmployeeRepository()
        .save(mapStructMapper.employeePostDTOToEmployee(employee));
  }

  public boolean updateEmployee(Employee employee, Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    if (!employeeOptional.isPresent())
      return false;

    employeeOptional.get().setName(employee.getName());
    repositoryService.getEmployeeRepository().save(employeeOptional.get());
    return true;
  }

  public boolean deleteEmployee(Integer employeeID) {
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
  public boolean assignProject(Integer employeeID, String projectID) {
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

  public boolean unassignProject(Integer employeeID, String projectID) {
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
  public Set<Task> getTasks(Integer employeeID) {
    Optional<Employee> employeeOptional =
        repositoryService.getEmployeeRepository().findById(employeeID);
    if (!employeeOptional.isPresent())
      return new HashSet<Task>();
    return repositoryService.getEmployeeRepository().findById(employeeID).get().getTasks();
  }

  public boolean assignTask(String taskID, Integer employeeID) {
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

  public boolean unassignTask(String taskID, Integer employeeID) {
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
}


