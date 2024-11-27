package com.airtribe.EmployeeManagementSystem.Service;

import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private EmployeeService employeeService;

    public boolean hasAccessToAdminEndpoint(Authentication authentication) throws EmployeeNotFoundException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        return "ADMIN".equals(employee.getDesignation());
    }

    public boolean hasAccessToManagerEndpoint(Authentication authentication) throws EmployeeNotFoundException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();
        return ("ADMIN".equals(role) || "MANAGER".equals(role));
    }

    public boolean hasAccessToManagerEndpoint(Authentication authentication, EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();

        if("MANAGER".equals(role)){
            return (employee.getDepartment().getName()).equals(employeeDTO.getDepartmentName());
        }
        return "ADMIN".equals(role);
    }

    public boolean hasAccessToManagerEndpoint(Authentication authentication, Long id) throws EmployeeNotFoundException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();

        if("MANAGER".equals(role)){
            return (employee.getDepartment().getName()).equals(employeeService.getEmployeeById(id).getDepartmentName());
        }
        return "ADMIN".equals(role);
    }

    public boolean hasAccessToManagerEndpoint(Authentication authentication, String email) throws EmployeeNotFoundException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();

        if("MANAGER".equals(role)){
            return (employee.getDepartment().getName()).equals(employeeService.getEmployeeByEmail(email).getDepartmentName());
        }
        return "ADMIN".equals(role);
    }

    public boolean hasAccessToEmployeeEndpoint(Authentication authentication, Long id) throws EmployeeNotFoundException{
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }
        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();

        if("EMPLOYEE".equals(role)){
            return employee.getEmployeeId() == id;
        }
        if("MANAGER".equals(role)){
            return (employee.getDepartment().getName()).equals(employeeService.getEmployeeById(id).getDepartmentName());
        }
        return "ADMIN".equals(role);
    }

    public boolean hasAccessToEmployeeEndpoint(Authentication authentication, String email) throws EmployeeNotFoundException{
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication instanceof JwtAuthenticationToken)){
            return false;
        }

        Employee employee = employeeService.getEmployeeByAuthentication(authentication);
        String role = employee.getDesignation();

        if("EMPLOYEE".equals(role)){
            return employee.getEmail().equals(email);
        }
        if("MANAGER".equals(role)){
            return (employee.getDepartment().getName()).equals(employeeService.getEmployeeByEmail(email).getDepartmentName());
        }
        return "ADMIN".equals(role);
    }

}