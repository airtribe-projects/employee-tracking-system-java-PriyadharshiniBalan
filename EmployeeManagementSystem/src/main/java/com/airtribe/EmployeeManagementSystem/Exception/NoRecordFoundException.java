package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoRecordFoundException extends Exception{
    public NoRecordFoundException(String message){
        super(message);
    }
}
