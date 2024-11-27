package com.airtribe.EmployeeManagementSystem.Utility;

import com.airtribe.EmployeeManagementSystem.DTOs.DepartmentDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DepartmentEntityMapper {

    public DepartmentDTO toDTO(Department department) {
        return new DepartmentDTO(
                department.getName(),
                department.getDescription(),
                department.getLocation(),
                department.getManager().getEmail()
        );
    }

    public Department toEntity(DepartmentDTO departmentDTO){
        return new Department(
                departmentDTO.getName(),
                departmentDTO.getDescription(),
                departmentDTO.getLocation(),
                null
        );
    }

    public List<DepartmentDTO> toDTOList(List<Department> departmentList) {
        List<DepartmentDTO> list = new ArrayList<>();
        for (Department department : departmentList) {
            DepartmentDTO dto = toDTO(department);
            list.add(dto);
        }
        return list;
    }

    public List<Department> toEntityList(List<DepartmentDTO> departmentDTOList){
        List<Department> list = new ArrayList<>();
        for (DepartmentDTO departmentDTO : departmentDTOList) {
            Department entity = toEntity(departmentDTO);
            list.add(entity);
        }
        return list;
    }
}

