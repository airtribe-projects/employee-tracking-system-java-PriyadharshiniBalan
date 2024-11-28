package com.airtribe.EmployeeManagementSystem.Service;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
//import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.DTOs.DepartmentDTO;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.airtribe.EmployeeManagementSystem.Utility.DepartmentEntityMapper;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository _departmentRepository;

    @Autowired
    private DepartmentEntityMapper _departmentEntityMapper;

    @Autowired
    private EmployeeRepository _employeeRepository;

    @Autowired
    private ProjectRepository _projectRepository;

    //@CacheEvict(value = "departmentCache", allEntries = true)
    public void addDepartments(@Valid DepartmentDTO departmentDto) throws EmployeeNotFoundException, ManagerRequirementException {
        String email = departmentDto.getManagerEmail();
        Employee manager = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        if(!manager.getDesignation().equals("MANAGER")){
            throw new ManagerRequirementException("The employee with email: " + email + " is not a manager role");
        }

        Optional<Department> optionalDepartment = _departmentRepository.findByManager(manager);
        if (optionalDepartment.isPresent()) {
            throw new ManagerRequirementException("The employee with email: " + email + " is already a Manager of Department : " + optionalDepartment.get().getName());
        }

        Department department = _departmentEntityMapper.toEntity(departmentDto);
        department.setManager(manager);
        _departmentRepository.save(department);

        manager.setDepartment(department);
        _employeeRepository.save(manager);
    }

    public List<DepartmentDTO> getDepartments() throws NoRecordFoundException {
        List<Department> departments = _departmentRepository.findAll();
        if(departments.isEmpty()){
            throw new NoRecordFoundException("No Department Records");
        }
        return _departmentEntityMapper.toDTOList(departments);
    }

    public DepartmentDTO getDepartmentById(Long id) throws DepartmentNotFoundException{
        return _departmentEntityMapper.toDTO(
                _departmentRepository.findById(id)
                        .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id)));
    }

    public DepartmentDTO getDepartmentByName(String departmentName) throws DepartmentNotFoundException {
        return _departmentEntityMapper.toDTO(
                _departmentRepository.findByName(departmentName)
                        .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName)));
    }

    public DepartmentDTO getDepartmentByManager(@Valid String email) throws EmployeeNotFoundException, DepartmentNotFoundException{
        Employee manager = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));
        return _departmentEntityMapper.toDTO(
                _departmentRepository.findByManager(manager)
                        .orElseThrow(() -> new DepartmentNotFoundException("Department not found with Manager email: " + email)));
    }

    public List<DepartmentDTO> getDepartmentByLocation(String location) throws DepartmentNotFoundException{
        List<Department> departments = _departmentRepository.findAllByLocation(location);
        if(departments.isEmpty()){
            throw new DepartmentNotFoundException("Department not found with Location: " + location);
        }
        return _departmentEntityMapper.toDTOList(departments);
    }

    public List<DepartmentDTO> getDepartmentsByProject(String projectName) throws ProjectNotFoundException, NoRecordFoundException {
        Project project = _projectRepository.findByName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: " + projectName));

        List<Department> departments = _departmentRepository.findAllByProject(project);
        if(departments.isEmpty()){
            throw new NoRecordFoundException("No departments found for project: "+ projectName);
        }
        return _departmentEntityMapper.toDTOList(departments);
    }

    public void updateDepartmentAllDetails(Long id, @Valid DepartmentDTO departmentDTO) throws DepartmentNotFoundException, EmployeeNotFoundException, ManagerRequirementException{
        Department department = _departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));

        String email = departmentDTO.getManagerEmail();
        Employee manager = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        if(!manager.getDesignation().equals("MANAGER")){
            throw new ManagerRequirementException("The employee with email: " + email + " is not a manager role");
        }

        Optional<Department> optionalDepartment = _departmentRepository.findByManager(manager);
        if (optionalDepartment.isPresent()) {
            throw new ManagerRequirementException("The employee with email: " + email + " is already a Manager of Department : " + optionalDepartment.get().getName());
        }

        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setLocation(department.getLocation());
        department.setManager(manager);
        _departmentRepository.save(department);

        manager.setDepartment(department);
        _employeeRepository.save(manager);
    }

    public void updateDepartmentName(Long id, @Valid String name) throws DepartmentNotFoundException{
        Department department = _departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        department.setName(name);
        _departmentRepository.save(department);
    }

    public void updateDepartmentData(String name, Map<String, String> updates) throws DepartmentNotFoundException{
        Department department = _departmentRepository.findByName(name)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + name));

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            switch (entry.getKey()){
                case "description":
                    department.setDescription(entry.getValue());
                    break;
                case "location":
                    department.setLocation(entry.getValue());
                    break;
                default:
                    break;
            }
        }
        _departmentRepository.save(department);
    }

    public void updateDepartmentManager(String name, @Email String email) throws EmployeeNotFoundException, DepartmentNotFoundException, ManagerRequirementException{
        Employee manager = _employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        if(!manager.getDesignation().equals("MANAGER")){
            throw new ManagerRequirementException("The employee with email: " + email + " is not a manager role");
        }

        Optional<Department> optionalDepartment = _departmentRepository.findByManager(manager);
        if (optionalDepartment.isPresent()) {
            throw new ManagerRequirementException("The employee with email: " + email + " is already a Manager of Department : " + optionalDepartment.get().getName());
        }

        Department department = _departmentRepository.findByName(name)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + name));
        department.setManager(manager);
        _departmentRepository.save(department);

        manager.setDepartment(department);
        _employeeRepository.save(manager);
    }

    @Transactional
    public void deleteDepartmentById(Long id) throws DepartmentNotFoundException {
        Department department = _departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));

        List<Employee> employeeList = _employeeRepository.findAllByDepartment(department);
        for(Employee employee : employeeList){
            employee.setDepartment(null);
        }
        _employeeRepository.saveAll(employeeList);
        _departmentRepository.delete(department);
    }

    @Transactional
    public void deleteDepartmentByName(String name) throws DepartmentNotFoundException{
        Department department = _departmentRepository.findByName(name)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + name));

        List<Employee> employeeList = _employeeRepository.findAllByDepartment(department);
        for(Employee employee : employeeList){
            employee.setDepartment(null);
        }
        _employeeRepository.saveAll(employeeList);
        _departmentRepository.delete(department);
    }
}
