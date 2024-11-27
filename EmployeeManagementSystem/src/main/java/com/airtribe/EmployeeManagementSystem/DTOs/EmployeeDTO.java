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
public class EmployeeDTO {

    @NotNull(message = "Employee Name is Mandatory")
    private String name;

    @NotNull(message = "Employee Designation is Mandatory")
    private String designation;

    @NotNull(message = "Employee Email is Mandatory")
    @Email(message = "Employee Email is not in correct format")
    private String email;

    private String departmentName;

    public EmployeeDTO(String name, String designation, String email) {
        this.name = name;
        this.designation = designation;
        this.email = email;
    }
}
