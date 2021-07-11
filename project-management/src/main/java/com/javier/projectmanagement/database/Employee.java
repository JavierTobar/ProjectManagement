package com.javier.projectmanagement.database;

import static javax.persistence.GenerationType.SEQUENCE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Employee")
@Table(name = "EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
  @Id
  @SequenceGenerator(name = "employeeID_sequence", sequenceName = "employeeID_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = SEQUENCE, generator = "employeeID_sequence")
  @Column(updatable = false)
  private Integer employeeID;
  @Column(nullable = false)
  private String name;
  @OneToMany(targetEntity = Task.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "employee_fk_id", referencedColumnName = "employeeid")
  // @JoinColumn(name = "employee_fk_name", referencedColumnName = "name")
  private Set<Task> tasks = new HashSet<>();

  @JsonIgnore
  @ManyToMany
  @JoinTable(name = "project_assignees", joinColumns = @JoinColumn(name = "project"),
      inverseJoinColumns = @JoinColumn(name = "employeeid"))
  private Set<Project> projects = new HashSet<>();
  @Column
  @ElementCollection(targetClass = String.class)
  private Set<String> projectnames = new HashSet<>();

  public Employee(String name) {
    super();
    this.name = name;
  }


  /**
   * Before this gets called, it's checked if this task's project is a project that this employee is
   * assigned to. We don't need to check for it here
   * 
   * @param t
   */
  public void addTask(Task t) {
    this.tasks.add(t);
  }

  /**
   * Before this gets called, it's checked if the project has enough space We don't need to check
   * for it here
   * 
   * @param p
   */
  public void addProject(Project p) {
    this.projectnames.add(p.getProject());
    this.projects.add(p);
  }

  /**
   * @return true if task existed and got removed, else false
   */
  public boolean removeTask(Task t) {
    if (this.tasks.contains(t)) {
      this.tasks.remove(t);
      return true;
    }
    return false;
  }

  /**
   * Also removes all the tasks that belonged to this project
   * 
   * @param p
   * @return true if project existed and got removed, else false
   */
  public boolean removeProject(Project p) {
    if (this.projects.contains(p)) {
      this.projectnames.remove(p.getProject());
      this.projects.remove(p);
      // Remove tasks belonging to the project that just got removed
      // Concurrent Modification solution
      List<Task> tempList = new ArrayList<>(this.tasks);
      for (Task t : tempList)
        if (t.getProject_fk().equals(p.getProject()))
          this.tasks.remove(t);
      return true;
    }
    return false;
  }

  /**
   * Helper function to determine if this employee belongs to the given project
   * 
   * @param p
   * @return
   */
  public boolean containsProject(String project) {
    for (Project temp : this.projects)
      if (temp.getProject().equals(project))
        return true;
    return false;
  }

  @Override
  public String toString() {
    return "Employee [employeeID=" + employeeID + ", name=" + name + ", tasks=" + tasks + "]";
  }


}
