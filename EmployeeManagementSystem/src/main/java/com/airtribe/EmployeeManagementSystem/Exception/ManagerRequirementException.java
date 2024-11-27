package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ManagerRequirementException extends Exception {
    public ManagerRequirementException(String message) {
        super(message);
    }
}