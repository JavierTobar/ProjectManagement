package com.javier.projectmanagement.mapstruct;

import com.javier.projectmanagement.database.Employee;
import com.javier.projectmanagement.database.Task;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-22T17:08:07-0400",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (Oracle Corporation)"
)
@Component
public class MapStructMapperImpl implements MapStructMapper {

    @Override
    public Employee employeePostDTOToEmployee(EmployeePostDTO employee) {
        if ( employee == null ) {
            return null;
        }

        Employee employee1 = new Employee();

        employee1.setName( employee.getName() );

        return employee1;
    }

    @Override
    public EmployeeGetDTO employeeToEmployeeGetDTO(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeGetDTO employeeGetDTO = new EmployeeGetDTO();

        employeeGetDTO.setEmployeeID( employee.getEmployeeID() );
        employeeGetDTO.setName( employee.getName() );
        Set<String> set = employee.getProjectnames();
        if ( set != null ) {
            employeeGetDTO.setProjectnames( new HashSet<String>( set ) );
        }
        Set<Task> set1 = employee.getTasks();
        if ( set1 != null ) {
            employeeGetDTO.setTasks( new HashSet<Task>( set1 ) );
        }

        return employeeGetDTO;
    }
}
