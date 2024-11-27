package com.airtribe.EmployeeManagementSystem.Controller;

import java.util.Map;
import java.util.List;

import com.airtribe.EmployeeManagementSystem.Exception.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.airtribe.EmployeeManagementSystem.DTOs.ProjectDTO;
import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.DTOs.DepartmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.airtribe.EmployeeManagementSystem.Service.ProjectService;
import com.airtribe.EmployeeManagementSystem.Service.EmployeeService;
import com.airtribe.EmployeeManagementSystem.Service.DepartmentService;

@RestController
public class DepartmentController {

    @Autowired
    private EmployeeService _employeeService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private DepartmentService _departmentService;

    //------------------------------------------ADMIN ENDPOINT -------------------------------------------------------//
    @PostMapping("/departments")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> addDepartments(@Valid @RequestBody DepartmentDTO departmentDTO) throws EmployeeNotFoundException, ManagerRequirementException {
        _departmentService.addDepartments(departmentDTO);
        return ResponseEntity.ok("Department Added Successfully");
    }

    @DeleteMapping("/departments/{id}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteDepartmentById(@PathVariable Long id) throws DepartmentNotFoundException{
        _departmentService.deleteDepartmentById(id);
        return ResponseEntity.ok("Department successfully deleted");
    }

    @DeleteMapping("/departments/name/{name}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteDepartmentByName(@PathVariable String name) throws DepartmentNotFoundException{
        _departmentService.deleteDepartmentByName(name);
        return ResponseEntity.ok("Department successfully deleted");
    }

    @PatchMapping("/departments/{name}/manager/{email}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateDepartmentManager(@PathVariable String name, @Email @PathVariable String email) throws EmployeeNotFoundException, DepartmentNotFoundException, ManagerRequirementException{
        _departmentService.updateDepartmentManager(name, email);
        return ResponseEntity.ok("Department Manager update successfully");
    }

    @PutMapping("/departments/{id}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateDepartmentAllDetails(@PathVariable Long id,@Valid @RequestBody DepartmentDTO departmentDTO) throws DepartmentNotFoundException, EmployeeNotFoundException, ManagerRequirementException{
        _departmentService.updateDepartmentAllDetails(id, departmentDTO);
        return ResponseEntity.ok("Department All Detail is Updated Successfully");
    }

    @PatchMapping("/departments/{id}/{name}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateDepartmentName(@PathVariable Long id,@PathVariable String name) throws DepartmentNotFoundException{
        _departmentService.updateDepartmentName(id, name);
        return ResponseEntity.ok("Department name Updated Successfully");
    }

    @PatchMapping("/departments/{name}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateDepartmentData(@PathVariable String name, @RequestParam Map<String, String> updates) throws DepartmentNotFoundException{
        _departmentService.updateDepartmentData(name, updates);
        return ResponseEntity.ok("Department detail Updated Successfully");
    }

    //--------------------------------------- ADMIN, MANAGER ENDPOINT -------------------------------------------------------//

    @GetMapping("/departments")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getAllDepartments() throws NoRecordFoundException {
        List<DepartmentDTO> departments = _departmentService.getDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) throws DepartmentNotFoundException {
        DepartmentDTO department = _departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/departments/search/name/{name}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentByName(@PathVariable String name) throws DepartmentNotFoundException {
        DepartmentDTO department = _departmentService.getDepartmentByName(name);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/departments/search/manager/{email}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentByManager(@Valid @PathVariable String email) throws DepartmentNotFoundException, EmployeeNotFoundException {
        DepartmentDTO department = _departmentService.getDepartmentByManager(email);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/departments/search/location/{location}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentByLocation(@PathVariable String location) throws DepartmentNotFoundException {
        List<DepartmentDTO> department = _departmentService.getDepartmentByLocation(location);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/departments/{name}/employees")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentEmployees(@PathVariable String name) throws DepartmentNotFoundException, NoRecordFoundException{
        List<EmployeeDTO> employeeDTOS = _employeeService.getEmployeesByDepartment(name);
        return ResponseEntity.ok(employeeDTOS);
    }

    @GetMapping("/departments/{name}/projects")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getDepartmentProjects(@PathVariable String name) throws DepartmentNotFoundException, NoRecordFoundException{
        List<ProjectDTO> projectDTOS = _projectService.getProjectsByDepartment(name);
        return ResponseEntity.ok(projectDTOS);
    }

    @GetMapping("/departments/{name}/projectBudget")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectBudgetForDepartment(@PathVariable String name) throws DepartmentNotFoundException{
        Long totalBudget = _projectService.getProjectBudgetForDepartment(name);
        return ResponseEntity.ok(totalBudget);
    }
}
