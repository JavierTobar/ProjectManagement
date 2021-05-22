package com.javier.projectmanagement.database;

import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class TodoTask {

  @Override
  public String toString() {
    return "TodoTask [todoTaskID=" + todoTaskID + ", description=" + description + ", dueDate="
        + dueDate + ", todoTaskPriority=" + todoTaskPriority + ", completed=" + completed + "]";
  }

  @Id
  @SequenceGenerator(name = "todoTask_sequence", sequenceName = "todoTask_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = SEQUENCE, generator = "todoTask_sequence")
  @Column(updatable = false)
  private Integer todoTaskID;

  @Column(nullable = false)
  private String description;

  private String dueDate;

  @Column(nullable = false)
  private TodoTaskPriorities todoTaskPriority;

  private boolean completed = false;

  public TodoTask(String description, String dueDate, TodoTaskPriorities todoTaskPriority) {
    this.description = description;
    this.dueDate = dueDate;
    this.todoTaskPriority = todoTaskPriority;
  }
}
