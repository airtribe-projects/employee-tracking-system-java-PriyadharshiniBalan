package com.airtribe.EmployeeManagementSystem.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRecordFoundException.class)
    public ErrorMessage handleNoEmployeeFoundException(NoRecordFoundException ex){
        return new ErrorMessage(HttpStatus.NO_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ErrorMessage handleEmployeeNotFoundException(EmployeeNotFoundException ex){
        return new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ErrorMessage handleDepartmentNotFoundException(DepartmentNotFoundException ex){
        return new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ErrorMessage handleProjectNotFoundException(ProjectNotFoundException ex){
        return new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ForeignKeyException.class)
    public ErrorMessage handleForeignKeyException(ForeignKeyException ex){
        return new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ManagerRequirementException.class)
    public ErrorMessage handleManagerRequirementException(ManagerRequirementException ex){
        return new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
