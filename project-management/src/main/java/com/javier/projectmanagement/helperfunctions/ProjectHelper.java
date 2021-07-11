package com.javier.projectmanagement.helperfunctions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.javier.projectmanagement.database.Project;
import com.javier.projectmanagement.database.RepositoryService;
import com.javier.projectmanagement.database.Task;

@Component
public class ProjectHelper {

  private static RepositoryService repositoryService;

  @Autowired
  RepositoryService rs;

  // Allows us to use a "static autowired field" indirectly
  @PostConstruct
  private void postConstruct() {
    ProjectHelper.repositoryService = rs;
  }

  public static void updateCriticalPath(Project project) {

    HashSet<Task> completed = new HashSet<Task>();
    HashSet<Task> remaining = new HashSet<Task>(project.getTasks());

    // backflow algorithm to find critical path
    while (!remaining.isEmpty()) {
      boolean progress = false;
      // find new task to calculate
      for (Iterator<Task> it = remaining.iterator(); it.hasNext();) {
        Task task = it.next();
        // happens when there was no predecessor for the ticket
        if (task.getPredesessorTaskID() == null || task.getPredesessorTaskID().isBlank()) {
          task.setCriticalCost(task.getDuration());
          completed.add(task);
          it.remove();
          progress = true;
        } else {
          Optional<Task> optionalTask =
              repositoryService.getTaskRepository().findById(task.getPredesessorTaskID());

          if (optionalTask.isPresent() && completed.contains(optionalTask.get())) {
            // all dependencines calculated, critical cost is max dependency

            double critical = optionalTask.get().getCriticalCost();
            task.setCriticalCost(critical + task.getDuration());
            completed.add(task);
            it.remove();
            progress = true;
          }
        }
      }
      // we get here if there's a cycle, i.e. deadlock in place
      if (!progress) {
        project.setCriticalCost(-1.0);
        return;
      }
    }

    double maxCost = 0;
    for (Task t : completed) {
      maxCost = t.getCriticalCost() > maxCost ? t.getCriticalCost() : maxCost;
    }
    project.setCriticalCost(maxCost);

    // set critical path
    Task[] path = completed.toArray(new Task[0]);
    Arrays.sort(path, new Comparator<Task>() {

      @Override
      public int compare(Task o1, Task o2) {
        // sort by cost
        int i = (int) (o2.getCriticalCost() - o1.getCriticalCost());
        if (i != 0)
          return i;

        // using dependency as a tie breaker
        // note if a is dependent on b then
        // critical cost a must be >= critical cost of b
        if (o1.getPredesessorTaskID() == o2.getTaskID())
          return -1;
        if (o2.getPredesessorTaskID() == o1.getTaskID())
          return 1;
        return 0;
      }
    });
    // Convert it to string representation
    String[] taskIDs = Arrays.stream(path).map(Task::getTaskID).toArray(String[]::new);
    project.setCriticalPath(taskIDs);
  }

}
