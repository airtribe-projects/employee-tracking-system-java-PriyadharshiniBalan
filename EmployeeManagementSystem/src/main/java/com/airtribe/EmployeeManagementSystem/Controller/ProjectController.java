package com.airtribe.EmployeeManagementSystem.Controller;

import com.airtribe.EmployeeManagementSystem.DTOs.DepartmentDTO;
import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.DTOs.ProjectDTO;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Service.DepartmentService;
import com.airtribe.EmployeeManagementSystem.Service.EmployeeService;
import com.airtribe.EmployeeManagementSystem.Service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private DepartmentService _departmentService;

    @Autowired
    private EmployeeService _employeeService;

    //------------------------------------------ADMIN END POINT -------------------------------------------------------//
    @PostMapping("/projects")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> addProjects(@Valid @RequestBody ProjectDTO projectDTO) throws EmployeeNotFoundException, DepartmentNotFoundException {
        _projectService.addProjects(projectDTO);
        return ResponseEntity.ok("Project Added Successfully");
    }

    @PutMapping("/projects/{id}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateProjectAllDetails(@PathVariable Long id,@Valid @RequestBody ProjectDTO projectDTO) throws ProjectNotFoundException, EmployeeNotFoundException, DepartmentNotFoundException{
        _projectService.updateProjectAllDetails(id, projectDTO);
        return ResponseEntity.ok("Project All Details Updated Successfully");
    }

    @PatchMapping("/projects/{name}/department/{deptName}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> updateProjectDepartment(@PathVariable String name, @PathVariable String deptName) throws DepartmentNotFoundException, ProjectNotFoundException{
        _projectService.updateProjectDepartment(name, deptName);
        return ResponseEntity.ok("Project Department updated successfully");
    }

    @DeleteMapping("/projects/{id}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id) throws ProjectNotFoundException, ForeignKeyException {
        _projectService.deleteProjectById(id);
        return ResponseEntity.ok("Project successfully deleted");
    }

    @DeleteMapping("/projects/name/{name}")
    @PreAuthorize("@securityService.hasAccessToAdminEndpoint(authentication)")
    public ResponseEntity<?> deleteProjectById(@PathVariable String name) throws ProjectNotFoundException, ForeignKeyException {
        _projectService.deleteProjectByName(name);
        return ResponseEntity.ok("Project successfully deleted");
    }

    //------------------------------------------ADMIN, MANAGER END POINT -------------------------------------------------------//
    @GetMapping("/projects")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getAllProjects() throws NoRecordFoundException {
        List<ProjectDTO> projectDTOS = _projectService.getProjects();
        return ResponseEntity.ok(projectDTOS);
    }

    @GetMapping("/projects/{id}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) throws ProjectNotFoundException {
        ProjectDTO project = _projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/projects/search/name/{name}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectByName(@PathVariable String name) throws ProjectNotFoundException {
        ProjectDTO project = _projectService.getProjectByName(name);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/projects/search/lead/{email}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectByLead(@Valid @PathVariable String email) throws ProjectNotFoundException, EmployeeNotFoundException {
        ProjectDTO project = _projectService.getProjectByLead(email);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/projects/{name}/employees")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectEmployees(@PathVariable String name) throws ProjectNotFoundException, NoRecordFoundException{
        List<EmployeeDTO> employees = _employeeService.getEmployeesByProject(name);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/projects/{name}/departments")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> getProjectDepartments(@PathVariable String name) throws ProjectNotFoundException, NoRecordFoundException{
        List<DepartmentDTO> departmentDTOS = _departmentService.getDepartmentsByProject(name);
        return ResponseEntity.ok(departmentDTOS);
    }

    @PatchMapping("/projects/{id}/{name}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> updateProjectName(@PathVariable Long id,@PathVariable String name) throws ProjectNotFoundException{
        _projectService.updateProjectName(id, name);
        return ResponseEntity.ok("Project name Updated Successfully");
    }

    @PatchMapping("/projects/{name}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> updateProjectData(@PathVariable String name, @RequestParam Map<String, String> updates) throws ProjectNotFoundException{
        _projectService.updateProjectData(name, updates);
        return ResponseEntity.ok("Project detail Updated Successfully");
    }

    @PatchMapping("/projects/{name}/lead/{email}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<?> updateProjectLead(@PathVariable String name, @Email @PathVariable String email) throws EmployeeNotFoundException, ProjectNotFoundException{
        _projectService.updateProjectLead(name, email);
        return ResponseEntity.ok("Project lead updated successfully");
    }

    @PostMapping("/projects/{id}/employees/{employeeId}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<String> addEmployeesToProject(@PathVariable Long id, @PathVariable List<Long> employeeId) throws ProjectNotFoundException, EmployeeNotFoundException {
        _projectService.addEmployeesToProject(id, employeeId);
        return ResponseEntity.ok("Employees added to project successfully!");
    }

    @DeleteMapping("/projects/{id}/employees/{employeeId}")
    @PreAuthorize("@securityService.hasAccessToManagerEndpoint(authentication)")
    public ResponseEntity<String> deleteEmployeesFromProject(@PathVariable Long id, @PathVariable List<Long> employeeId) throws ProjectNotFoundException, EmployeeNotFoundException {
        _projectService.removeEmployeesToProject(id, employeeId);
        return ResponseEntity.ok("Employees removed from project successfully!");
    }
}
