package com.airtribe.EmployeeManagementSystem.Controller;

import java.util.Map;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.Service.EmployeeService;
import com.airtribe.EmployeeManagementSystem.Service.DepartmentService;
import com.airtribe.EmployeeManagementSystem.Exception.ForeignKeyException;
import com.airtribe.EmployeeManagementSystem.Exception.NoRecordFoundException;
import com.airtribe.EmployeeManagementSystem.Exception.EmployeeNotFoundException;
import com.airtribe.EmployeeManagementSystem.Exception.DepartmentNotFoundException;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService _employeeService;

    @Autowired
    private DepartmentService _departmentService;

    @GetMapping("/admin")
    public ResponseEntity<?> hello(){
        return ResponseEntity.ok("Hello! Greetings from Employee Management System");
    }

    //------------------------------------------ADMIN END POINT -------------------------------------------------------//
    @GetMapping("/employees")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> getAllEmployees() throws NoRecordFoundException {
        List<EmployeeDTO> employees = _employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/search/name/{name}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> getAllEmployeeByName(@PathVariable String name) throws NoRecordFoundException {
        List<EmployeeDTO> employees = _employeeService.getEmployeeByName(name);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/search/designation/{designation}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> getAllEmployeeByDesignation(@PathVariable String designation) throws NoRecordFoundException {
        List<EmployeeDTO> employees = _employeeService.getEmployeesByDesignation(designation);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/search/department/{departmentName}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> getAllEmployeeByDepartment(@PathVariable String departmentName) throws DepartmentNotFoundException, NoRecordFoundException {
        List<EmployeeDTO> employees = _employeeService.getEmployeesByDepartment(departmentName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/noProjects")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> getAllEmployeeWithoutProjects(){
        List<EmployeeDTO> employees = _employeeService.getEmployeesWithoutProjects();
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteEmployeeById(@PathVariable Long id) throws EmployeeNotFoundException, ForeignKeyException {
        _employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok("Employee successfully Deleted");
    }

    @DeleteMapping("/employees/email/{email}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteEmployeeByEmail(@PathVariable String email) throws EmployeeNotFoundException, ForeignKeyException{
        _employeeService.deleteEmployeeByEmail(email);
        return ResponseEntity.ok("Employee successfully Deleted");
    }

    //------------------------------------------ADMIN, MANAGER ENDPOINT -------------------------------------------------------//

    @PostMapping("/employees")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication, #employeeDTO)")
    public ResponseEntity<?> addEmployees(@Valid @RequestBody EmployeeDTO employeeDTO) throws DepartmentNotFoundException{
        _employeeService.addEmployees(employeeDTO);
        return ResponseEntity.ok("Employee Added Successfully");
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication, #id)")
    public ResponseEntity<?> updateEmployeeAllDetails(@PathVariable Long id,@Valid @RequestBody EmployeeDTO employeeDTO) throws EmployeeNotFoundException, DepartmentNotFoundException{
        _employeeService.updateEmployeeAllDetails(id, employeeDTO);
        return ResponseEntity.ok("Employee All Details Updated Successfully");
    }

    @PatchMapping("/employees/{id}/{email}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication, #id)")
    public ResponseEntity<?> updateEmployeeEmail(@PathVariable Long id,@Valid @PathVariable String email) throws EmployeeNotFoundException{
        _employeeService.updateEmployeeEmail(id, email);
        return ResponseEntity.ok("Employee Email Updated Successfully");
    }

    @PatchMapping("/employees/{email}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication, #email)")
    public ResponseEntity<?> updateEmployeeData(@Valid @PathVariable String email, @RequestParam Map<String, String> updates) throws EmployeeNotFoundException{
        _employeeService.updateEmployeeData(email, updates);
        return ResponseEntity.ok("Employee detail Updated Successfully");
    }

    @PatchMapping("/employees/{email}/department/{departmentName}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication, #email)")
    public ResponseEntity<?> updateEmployeeDepartment(@Valid @PathVariable String email,@PathVariable String departmentName) throws DepartmentNotFoundException, EmployeeNotFoundException{
        _employeeService.updateEmployeeDepartment(email,departmentName);
        return ResponseEntity.ok("Employee Department Updated Successfully");
    }

    //--------------------------------------ADMIN, MANAGER, EMPLOYEE ENDPOINT ---------------------------------------------------//
    @GetMapping("/employees/{id}")
    @PreAuthorize("@securityService.hasAccessToEmployeeEndpoint(authentication, #id)")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) throws EmployeeNotFoundException {
        EmployeeDTO employees = _employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/search/email/{email}")
    @PreAuthorize("@securityService.hasAccessToEmployeeEndpoint(authentication, #email)")
    public ResponseEntity<?> getEmployeeByEmail(@Email @PathVariable String email) throws EmployeeNotFoundException{
        EmployeeDTO employees = _employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employees);
    }
}
