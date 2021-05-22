package com.javier.projectmanagement.mapstruct;

import org.mapstruct.Mapper;
import com.javier.projectmanagement.database.Employee;


@Mapper(componentModel = "spring")
public interface MapStructMapper {

  Employee employeePostDTOToEmployee(EmployeePostDTO employee);

  EmployeeGetDTO employeeToEmployeeGetDTO(Employee employee);


}
