package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}