package com.javier.projectmanagement.database;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.javier.projectmanagement.helperfunctions.ProjectHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "Project")
@Getter
@Setter
@NoArgsConstructor
public class Project {

  @Id
  @Column(updatable = false)
  private String project;
  @Column(nullable = false)
  private String description;
  @Column(updatable = false, nullable = false)
  private String startDate;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ProjectStatus status = ProjectStatus.NOT_STARTED; // default value
  @Column(nullable = false)
  private Integer devsLimit;
  private Double criticalCost; // -1 if N/A
  @OneToMany(targetEntity = Task.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "project_fk", referencedColumnName = "project")
  private Set<Task> tasks = new HashSet<>();
  @ManyToMany(mappedBy = "projects")
  private Set<Employee> employees = new HashSet<>();
  private String[] criticalPath;

  public Project(String project, String description, String startDate, Integer devsLimit) {
    super();
    this.project = project;
    this.description = description;
    this.startDate = startDate;
    this.devsLimit = devsLimit;
    this.criticalCost = 0.0;
  }

  public void addTask(Task t) {
    this.tasks.add(t);
  }

  // Gets called whenever a task is updated/created/assigned
  // Sets to -1 if can't be calculated, i.e. deadlock/cycle
  // there is a possibility of a deadlock despite trying to avoid it in TaskController
  // Consider the following scenario:
  // Task A has Task B as predecessor and task C as successor
  // Task C has task B as predecessor
  // (this is allowed, it is only not allowed to have task A as it's predecessor)
  // logic for array of dependencies rather than singular:
  // https://stackoverflow.com/questions/2985317/critical-path-method-algorithm
  public void updateCriticalPath() {
    ProjectHelper.updateCriticalPath(this);
  }


  /**
   * This simply removes the task from project, doesn't get rid of the assignee assigned to the task
   * 
   * @param t
   * @return true if task exists and got removed, else false
   */
  public boolean removeTask(Task t) {
    if (this.tasks.contains(t)) {
      this.tasks.remove(t);
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "Project [project=" + project + ", description=" + description + "]";
  }
}
