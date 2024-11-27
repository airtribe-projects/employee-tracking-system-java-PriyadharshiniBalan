package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DepartmentNotFoundException extends Exception{
    public DepartmentNotFoundException(String message){
        super(message);
    }
}
