package com.airtribe.EmployeeManagementSystem.Utility;

import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeEntityMapper {

    public EmployeeDTO toDTO(Employee employee) {
        if (employee.getDepartment() != null){
            return new EmployeeDTO(
                    employee.getName(),
                    employee.getDesignation().toUpperCase(),
                    employee.getEmail(),
                    employee.getDepartment().getName()
            );
        }
        return new EmployeeDTO(
                employee.getName(),
                employee.getDesignation().toUpperCase(),
                employee.getEmail()
        );

    }

    public Employee toEntity(EmployeeDTO employeeDTO) {
        return new Employee(
                employeeDTO.getName(),
                employeeDTO.getDesignation().toUpperCase(),
                employeeDTO.getEmail()
        );
    }

    public List<EmployeeDTO> toDTOList(List<Employee> employeeList) {
        List<EmployeeDTO> list = new ArrayList<>();
        for (Employee employee : employeeList) {
            EmployeeDTO dto = toDTO(employee);
            list.add(dto);
        }
        return list;
    }

    public List<Employee> toEntityList(List<EmployeeDTO> employeeDTOs) {
        List<Employee> list = new ArrayList<>();
        for (EmployeeDTO employeeDTO : employeeDTOs) {
            Employee entity = toEntity(employeeDTO);
            list.add(entity);
        }
        return list;
    }
}

