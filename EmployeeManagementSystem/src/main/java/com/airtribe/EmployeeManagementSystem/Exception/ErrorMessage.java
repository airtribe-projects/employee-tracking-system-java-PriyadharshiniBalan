package com.airtribe.EmployeeManagementSystem.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorMessage {
    private HttpStatus statusCode;
    private String message;
    private StackTraceElement[] stackTrace;

    public ErrorMessage(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.stackTrace = null;
    }
}
