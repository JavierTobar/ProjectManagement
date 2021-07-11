package com.javier.projectmanagement.helperfunctions;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;

@Component
public class TaskHelper {

  private static RepositoryService repositoryService;

  @Autowired
  RepositoryService rs;

  // Allows us to use a "static autowired field" indirectly
  @PostConstruct
  private void postConstruct() {
    TaskHelper.repositoryService = rs;
  }

  // @Autowired
  // public TaskHelper(RepositoryService repositoryService) {
  // TaskHelper.repositoryService = repositoryService;
  // }

  public static void updateDuration(Task task) {
    // update duration
    int totalWeight =
        task.getOptimisticWeight() + task.getRealisticWeight() + task.getPessimisticWeight();
    double duration =
        (double) Math
            .round(
                100.0
                    * (task.getOptimisticEstimation() * 1.0 * task.getOptimisticWeight()
                        + task.getRealisticEstimation() * task.getRealisticWeight()
                        + task.getPessimisticEstimation() * task.getPessimisticWeight())
                    / totalWeight)
            / 100.0;
    task.setDuration(duration);
  }



  public static void updateCriticalCost(Task task) {
    // if no predecessor, then critical cost is just the task itself
    if (task.getPredesessorTaskID() == null) {
      task.setCriticalCost(task.getDuration());
      return;
    }
    // update criticalCost
    Optional<Task> taskOptional =
        repositoryService.getTaskRepository().findById(task.getPredesessorTaskID());
    // if no task, then criticalCost = duration of this task
    if (!taskOptional.isPresent()) {
      task.setCriticalCost(task.getDuration());
      return;
    }
    // else we need to aggregate dependency critical cost to this task's innate cost
    task.setCriticalCost(taskOptional.get().getCriticalCost() + task.getDuration());
  }



}
