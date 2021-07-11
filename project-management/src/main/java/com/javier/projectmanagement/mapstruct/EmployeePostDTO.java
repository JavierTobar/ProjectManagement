package com.javier.projectmanagement.mapstruct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeePostDTO {
  @JsonProperty("name")
  private String name;
}
