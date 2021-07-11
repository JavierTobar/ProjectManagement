package com.javier.projectmanagement.mapstruct;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javier.projectmanagement.database.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeGetDTO {

  @JsonProperty("employeeID")
  private Integer employeeID;

  @JsonProperty("name")
  private String name;

  @JsonProperty("projectnames")
  private Set<String> projectnames = new HashSet<>();

  @JsonProperty("tasks")
  private Set<Task> tasks = new HashSet<>();

}
