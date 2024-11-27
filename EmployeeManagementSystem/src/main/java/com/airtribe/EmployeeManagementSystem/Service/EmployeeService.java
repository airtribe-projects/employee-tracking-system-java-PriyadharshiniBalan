package com.airtribe.EmployeeManagementSystem.Service;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.Utility.EmployeeEntityMapper;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository _employeeRepository;

    @Autowired
    private DepartmentRepository _departmentRepository;

    @Autowired
    private ProjectRepository _projectRepository;

    @Autowired
    private EmployeeEntityMapper _employeeEntityMapper;

    //@CacheEvict(value = "employeesCache", allEntries = true)
    public void addEmployees(@Valid EmployeeDTO employeeDTO) throws DepartmentNotFoundException{
        Department department = null;
        if(employeeDTO.getDepartmentName()!=null){
            String departmentName = employeeDTO.getDepartmentName();
             department = _departmentRepository.findByName(departmentName)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName));

        }
        Employee employee = _employeeEntityMapper.toEntity(employeeDTO);
        employee.setDepartment(department);
        _employeeRepository.save(employee);
    }

    //@Cacheable(value = "employeesCache")
    public List<EmployeeDTO> getAllEmployees() throws NoRecordFoundException {
        List<Employee> employees = _employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new NoRecordFoundException("No employees found.");
        }
        return _employeeEntityMapper.toDTOList(employees);
    }

    //@Cacheable(value = "employeesCache", key = "#id")
    public EmployeeDTO getEmployeeById(Long id) throws EmployeeNotFoundException {
        return _employeeEntityMapper.toDTO(
                _employeeRepository.findById(id)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id)));
    }

    public EmployeeDTO getEmployeeByEmail(String email) throws EmployeeNotFoundException{
        return _employeeEntityMapper.toDTO(
                _employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email)
                        ));
    }

    public List<EmployeeDTO> getEmployeeByName(String name) throws NoRecordFoundException {
        List<Employee> employees = _employeeRepository.findAllByName(name);
        if (employees.isEmpty()) {
            throw new NoRecordFoundException("No employees found with name: " + name);
        }
        return _employeeEntityMapper.toDTOList(employees);
    }

    public List<EmployeeDTO> getEmployeesByDesignation(String designation) throws NoRecordFoundException {
        List<Employee> employees = _employeeRepository.findAllByDesignation(designation);
        if (employees.isEmpty()) {
            throw new NoRecordFoundException("No employees found with designation: " + designation);
        }
        return _employeeEntityMapper.toDTOList(employees);
    }

    //@Cacheable(value = "employeesByDepartmentCache", key = "#departmentName")
    public List<EmployeeDTO> getEmployeesByDepartment(String departmentName) throws NoRecordFoundException , DepartmentNotFoundException{
        Department department = _departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName));

        List<Employee> employees = _employeeRepository.findAllByDepartment(department);
        if (employees.isEmpty()) {
            throw new NoRecordFoundException("No employees found with department: " + departmentName);
        }
        return _employeeEntityMapper.toDTOList(employees);
    }

    //@Cacheable(value = "employeesByProjectCache", key = "#projectName")
    public List<EmployeeDTO> getEmployeesByProject(String projectName) throws NoRecordFoundException, ProjectNotFoundException {
        Project project = _projectRepository.findByName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ projectName));

        List<Employee> employees = _employeeRepository.findAllByProject(project);
        if(employees.isEmpty()){
            throw new NoRecordFoundException("No employees found in project: " + projectName);
        }
        return _employeeEntityMapper.toDTOList(employees);
    }

    //@Cacheable(value = "employeesWithoutProjectsCache")
    public List<EmployeeDTO> getEmployeesWithoutProjects() {
        List<Employee> employees = _employeeRepository.findAllWithoutProjects();
        return _employeeEntityMapper.toDTOList(employees);
    }

    @Transactional
   // @CacheEvict(value = "employeesCache", key = "#id")
    public void deleteEmployeeById(Long id) throws EmployeeNotFoundException, ForeignKeyException {
        Employee employee = _employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        Optional<Department> optionalDepartment = _departmentRepository.findByManager(employee);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            if (department.getManager() != null) {
                throw new ForeignKeyException("Given Employee Id " +id + " is a Manager of a department "+ department.getName()+". Cannot delete Employee. Change Manager and then try");
            }
        }
        _employeeRepository.delete(employee);
    }

    @Transactional
    public void deleteEmployeeByEmail(String email) throws EmployeeNotFoundException, ForeignKeyException{
        Employee employee = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        Optional<Department> optionalDepartment = _departmentRepository.findByManager(employee);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            if (department.getManager() != null) {
                throw new ForeignKeyException("Given Employee email " + email + " is a Manager of a department "+ department.getName()+". Cannot delete Employee. Change Manager and then try");
            }
        }
        _employeeRepository.delete(employee);
    }

    //@CachePut(value = "employeesCache", key = "#id")
    public void updateEmployeeAllDetails(Long id, EmployeeDTO employeeDTO) throws EmployeeNotFoundException, DepartmentNotFoundException{
        if(employeeDTO.getDepartmentName() == null){
            throw new DepartmentNotFoundException("Department cannot be empty");
        }
        String departmentName = employeeDTO.getDepartmentName();
        Department department = _departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName));

        Employee employee = _employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        employee.setEmail(employeeDTO.getEmail());
        employee.setName(employeeDTO.getName());
        employee.setDesignation(employeeDTO.getDesignation().toUpperCase());
        employee.setDepartment(department);
        _employeeRepository.save(employee);
    }

    public void updateEmployeeEmail(Long id, @Valid String email) throws EmployeeNotFoundException{
        Employee employee = _employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        employee.setEmail(email);
        _employeeRepository.save(employee);
    }

    public void updateEmployeeData(@Valid String email, Map<String, String> updates) throws EmployeeNotFoundException{
        Employee employee = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            switch (entry.getKey()){
                case "name":
                    employee.setName(entry.getValue());
                    break;
                case "designation":
                    employee.setDesignation(entry.getValue().toUpperCase());
                    break;
                default:
                    break;
            }
        }
        _employeeRepository.save(employee);
    }

    public void updateEmployeeDepartment(@Valid String email, String departmentName) throws EmployeeNotFoundException, DepartmentNotFoundException{
        Department department = _departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName));

        Employee employee = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));
        employee.setDepartment(department);
        _employeeRepository.save(employee);
    }

    public Employee getEmployeeByAuthentication(Authentication authentication) throws EmployeeNotFoundException{
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = jwtToken.getToken().getClaims();
        String userEmail = (String) claims.get("email");
        Employee employeeAu = _employeeRepository.findByEmail(userEmail)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not found with Email: " + userEmail));
        return employeeAu;
    }
}
