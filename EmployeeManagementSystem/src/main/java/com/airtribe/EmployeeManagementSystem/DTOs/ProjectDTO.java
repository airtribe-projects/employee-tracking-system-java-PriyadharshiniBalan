package com.airtribe.EmployeeManagementSystem.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    @NotNull(message = "Project Name is Mandatory")
    private String name;

    @NotNull(message = "Project Description is Mandatory")
    private String description;

    @NotNull(message = "Project Budget is Mandatory")
    private Long budget;

    @NotNull(message = "Project must have a Lead Email")
    @Email(message = "Lead Email is not in correct format")
    private String leadEmail;

    @NotNull(message = "Project must be linked with a Department")
    private String departmentName;
}
