package com.javier.projectmanagement.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Getter;

@Service
@Getter
public class RepositoryService {
  @Autowired
  TaskRepository taskRepository;

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  EmployeeRepository employeeRepository;

  @Autowired
  TodoTaskRepository todoTaskRepository;

}
