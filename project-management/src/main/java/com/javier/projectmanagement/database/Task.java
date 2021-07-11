package com.javier.projectmanagement.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.javier.projectmanagement.helperfunctions.TaskHelper;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TASK")
@Getter
@Setter
@NoArgsConstructor
public class Task {

  @Id
  @Column(updatable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskID_sequence")
  @GenericGenerator(name = "taskID_sequence",
      strategy = "com.javier.projectmanagement.database.StringPrefixedSequenceIdGenerator",
      parameters = {
          @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
          @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER,
              value = "Task_"),
          @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER,
              value = "%04d")})
  private String taskID;
  private String description;
  @Column(updatable = false)
  private String project_fk;
  private Integer employee_fk_id;
  private String employee_fk_name;
  private String predesessorTaskID;
  private String successorTaskID;

  private Double duration;
  private Double criticalCost;

  private Integer optimisticWeight = 1;
  private Integer realisticWeight = 4;
  private Integer pessimisticWeight = 1;
  @Column(nullable = false)
  private Integer optimisticEstimation;
  @Column(nullable = false)
  private Integer realisticEstimation;
  @Column(nullable = false)
  private Integer pessimisticEstimation;

  @Override
  public String toString() {
    return "Task [taskID=" + taskID + ", description=" + description + "]";
  }


  public Task(String description) {
    super();
    this.description = description;
  }

  public Task(String description, Integer optimisticEstimation, Integer realisticEstimation,
      Integer pessimisticEstimation) {
    super();
    this.description = description;
    this.optimisticEstimation = optimisticEstimation;
    this.realisticEstimation = realisticEstimation;
    this.pessimisticEstimation = pessimisticEstimation;
    updateDurationAndCriticalCost();
  }

  /**
   * We need this because we can't have Autowired and Transient annotations together
   */
  public void updateDurationAndCriticalCost() {
    TaskHelper.updateDuration(this);
    TaskHelper.updateCriticalCost(this);
  }

  /**
   * update only if it's a worse critical cost, else don't touch it
   * 
   * @param criticalCost
   */
  public void setCriticalCost(double criticalCost) {
    if (this.criticalCost == null)
      this.criticalCost = criticalCost;
    else if (criticalCost > this.criticalCost)
      this.criticalCost = criticalCost;
  }



}
