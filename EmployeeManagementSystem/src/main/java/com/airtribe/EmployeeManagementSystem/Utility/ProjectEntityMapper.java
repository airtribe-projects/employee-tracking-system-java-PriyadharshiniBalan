package com.airtribe.EmployeeManagementSystem.Utility;

import com.airtribe.EmployeeManagementSystem.DTOs.ProjectDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectEntityMapper {

    public ProjectDTO toDTO(Project project) {
        return new ProjectDTO(
                project.getName(),
                project.getDescription(),
                project.getBudget(),
                project.getLead().getEmail(),
                project.getDepartment().getName()
        );
    }

    public Project toEntity(ProjectDTO projectDTO){
        return new Project(
                projectDTO.getName(),
                projectDTO.getDescription(),
                projectDTO.getBudget(),
                null,
                null
        );
    }

    public List<ProjectDTO> toDTOList(List<Project> projectList) {
        List<ProjectDTO> list = new ArrayList<>();
        for (Project project : projectList) {
            ProjectDTO dto = toDTO(project);
            list.add(dto);
        }
        return list;
    }

    public List<Project> toEntityList(List<ProjectDTO> projectDTOList){
        List<Project> list = new ArrayList<>();
        for (ProjectDTO projectDTO : projectDTOList) {
            Project entity = toEntity(projectDTO);
            list.add(entity);
        }
        return list;
    }
}

