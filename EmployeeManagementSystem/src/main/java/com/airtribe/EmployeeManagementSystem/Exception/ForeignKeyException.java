package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ForeignKeyException extends Exception{
    public ForeignKeyException(String message){
        super(message);
    }
}
