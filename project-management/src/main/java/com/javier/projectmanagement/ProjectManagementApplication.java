package com.javier.projectmanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.EmployeeRepository;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.ProjectRepository;
import com.javier.projectmanagement.database.Task;
import com.javier.projectmanagement.database.TaskRepository;
import com.javier.projectmanagement.database.TodoTask;
import com.javier.projectmanagement.database.TodoTaskPriorities;
import com.javier.projectmanagement.database.TodoTaskRepository;

@SpringBootApplication
public class ProjectManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProjectManagementApplication.class, args);
  }

  @Bean
  CommandLineRunner createInitialData(ProjectRepository projectRepository,
                                      TaskRepository taskRepository, EmployeeRepository employeeRepository,
                                      TodoTaskRepository todoTaskRepository) {
    return args -> {
      // Create 4 projects
      Project p1 = new Project("DeepMind", "An interesting AI", "2021-01-30", 2);
      Project p2 = new Project("Apollo", "Commercial spaceflight", "2021-02-16", 2);
      // Project p3 = new Project("Titan", "Explore Saturn's biggest moon", "2021-03-17", 2);
      // Project p4 = new Project("Atlantis", "Explore the lost city of Atlantis", "2021-05-01", 6);

      // Create 8 tasks
      Task t1 = new Task("Fix backend bug", 2, 4, 8);
      Task t2 = new Task("Improve UI for XYZ", 3, 5, 8);
      Task t3 = new Task("Add additional feature", 8, 12, 24);
      // Task t4 = new Task("description 4", 2, 5, 10);
      // Task t5 = new Task("description 5", 1, 2, 4);
      // Task t6 = new Task("description 6", 4, 6, 8);
      // Task t7 = new Task("description 7", 6, 9, 12);
      // Task t8 = new Task("description 8", 10, 14, 20);

      // Create 11 employees
      Employee e1 = new Employee("Louise Robins");
      Employee e2 = new Employee("Bruce Barrows");
      Employee e3 = new Employee("Wade Kline");
      Employee e4 = new Employee("Yasmin Jensen");
      Employee e5 = new Employee("Tom Avalos");
      // Employee e6 = new Employee("Carmen Soto");
      // Employee e7 = new Employee("Lisa Sanchez");
      // Employee e8 = new Employee("Claude Mills");
      // Employee e9 = new Employee("Cleo Reeve");
      // Employee e10 = new Employee("Heather Hammond");
      // Employee e11 = new Employee("Thomas Harris");

      TodoTask td1 = new TodoTask("Sync up w/ Maria", "2021-03-16", TodoTaskPriorities.MEDIUM);
      TodoTask td2 = new TodoTask("Demo w/ interns", "2021-05-14", TodoTaskPriorities.HIGH);
      TodoTask td3 =
          new TodoTask("Review technical writeups", "2021-06-22", TodoTaskPriorities.LOW);

      todoTaskRepository.save(td1);
      todoTaskRepository.save(td2);
      todoTaskRepository.save(td3);



      // Add tasks to existing projects
      p1.addTask(t1);
      p1.addTask(t2);
      p1.addTask(t3);
      // p3.addTask(t4);
      // p3.addTask(t5);
      // p4.addTask(t6);
      // p4.addTask(t7);
      // p4.addTask(t8);

      // Save tasks
      taskRepository.save(t1);
      taskRepository.save(t2);
      taskRepository.save(t3);
      // taskRepository.save(t4);
      // taskRepository.save(t5);
      // taskRepository.save(t6);
      // taskRepository.save(t7);
      // taskRepository.save(t8);

      // Save projects
      projectRepository.save(p1);
      projectRepository.save(p2);
      // projectRepository.save(p3);
      // projectRepository.save(p4);

      // Save employees
      employeeRepository.save(e1);
      employeeRepository.save(e2);
      employeeRepository.save(e3);
      employeeRepository.save(e4);
      employeeRepository.save(e5);
      // employeeRepository.save(e6);
      // employeeRepository.save(e7);
      // employeeRepository.save(e8);
      // employeeRepository.save(e9);
      // employeeRepository.save(e10);
      // employeeRepository.save(e11);

    };
  }
}
