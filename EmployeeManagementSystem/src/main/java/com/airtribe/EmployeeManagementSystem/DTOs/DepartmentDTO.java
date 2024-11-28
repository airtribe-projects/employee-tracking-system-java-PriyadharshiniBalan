package com.airtribe.EmployeeManagementSystem.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    @NotNull(message = "Department Name is Mandatory")
    private String name;

    @NotNull(message = "Department Description is Mandatory")
    private String description;

    @NotNull(message = "Department location is Mandatory")
    private String location;

    @NotNull(message = "Department must have a Manager Email")
    @Email(message = "Manager Email is not in correct format")
    private String managerEmail;
}
