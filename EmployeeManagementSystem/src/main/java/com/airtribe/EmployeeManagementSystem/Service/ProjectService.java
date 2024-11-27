package com.airtribe.EmployeeManagementSystem.Service;

import com.airtribe.EmployeeManagementSystem.DTOs.ProjectDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import com.airtribe.EmployeeManagementSystem.Utility.ProjectEntityMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository _projectRepository;

    @Autowired
    private EmployeeRepository _employeeRepositpry;

    @Autowired
    private DepartmentRepository _departmentRepository;

    @Autowired
    private ProjectEntityMapper _projectEntityMapper;

    public void addProjects(@Valid ProjectDTO projectDTO) throws EmployeeNotFoundException, DepartmentNotFoundException {
        String email = projectDTO.getLeadEmail();
        Employee lead = _employeeRepositpry.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));
        String deptName = projectDTO.getDepartmentName();
        Department department = _departmentRepository.findByName(deptName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + deptName));
        Project project = _projectEntityMapper.toEntity(projectDTO);
        project.setLead(lead);
        project.setDepartment(department);
        _projectRepository.save(project);
    }

    public List<ProjectDTO> getProjects() throws NoRecordFoundException{
        List<Project> projectList = _projectRepository.findAll();
        if(projectList.isEmpty()){
            throw new NoRecordFoundException("No Project Records");
        }
        return _projectEntityMapper.toDTOList(projectList);
    }

    public ProjectDTO getProjectById(Long id) throws ProjectNotFoundException {
        return _projectEntityMapper.toDTO(
                _projectRepository.findById(id)
                        .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id)));
    }

    public ProjectDTO getProjectByName(String name) throws ProjectNotFoundException{
        return _projectEntityMapper.toDTO(
                _projectRepository.findByName(name)
                        .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ name)));
    }

    public ProjectDTO getProjectByLead(@Valid String email) throws EmployeeNotFoundException, ProjectNotFoundException {
        Employee lead = _employeeRepositpry.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));
        return _projectEntityMapper.toDTO(
                _projectRepository.findByLead(lead)
                        .orElseThrow(() -> new ProjectNotFoundException("Project not found with Lead email: " + email)));
    }

    public List<ProjectDTO> getProjectsByDepartment(String departmentName) throws DepartmentNotFoundException, NoRecordFoundException{
        Department department = _departmentRepository.findByName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + departmentName));

        List<Project> projectList = _projectRepository.findAllByDepartment(department);
        if(projectList.isEmpty()){
            throw new NoRecordFoundException("No Projects found for department: " + departmentName);
        }
        return _projectEntityMapper.toDTOList(projectList);
    }

    public void updateProjectAllDetails(Long id, @Valid ProjectDTO projectDTO) throws ProjectNotFoundException, EmployeeNotFoundException, DepartmentNotFoundException{
        Project project = _projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id));

        String email = projectDTO.getLeadEmail();
        Employee lead = _employeeRepositpry.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        String deptName = projectDTO.getDepartmentName();
        Department department = _departmentRepository.findByName(deptName)
                        .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: "+ deptName));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setBudget(projectDTO.getBudget());
        project.setLead(lead);
        project.setDepartment(department);
        _projectRepository.save(project);
    }

    public void updateProjectName(Long id, String name) throws ProjectNotFoundException{
        Project project = _projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id));
        project.setName(name);
        _projectRepository.save(project);
    }

    public void updateProjectData(String name, Map<String, String> updates) throws ProjectNotFoundException{
        Project project = _projectRepository.findByName(name)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ name));

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            switch (entry.getKey()){
                case "description":
                    project.setDescription(entry.getValue());
                    break;
                case "budget":
                    project.setBudget(Long.parseLong(entry.getValue()));
                    break;
                default:
                    break;
            }
        }

        _projectRepository.save(project);
    }

    public void updateProjectLead(String name, @Email String email) throws EmployeeNotFoundException, ProjectNotFoundException{
        Employee lead = _employeeRepositpry.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with email: " + email));

        Project project = _projectRepository.findByName(name)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ name));

        project.setLead(lead);
        _projectRepository.save(project);
    }

    public void updateProjectDepartment(String name, String deptName) throws  ProjectNotFoundException, DepartmentNotFoundException{
        Department department = _departmentRepository.findByName(deptName)
                .orElseThrow(()-> new DepartmentNotFoundException("Department not found with name: "+ deptName));

        Project project = _projectRepository.findByName(name)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ name));

        project.setDepartment(department);
        _projectRepository.save(project);
    }

    public void addEmployeesToProject(Long id, List<Long> employeeIds) throws ProjectNotFoundException, EmployeeNotFoundException{
        Project project = _projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id));

        for(Long employeeId: employeeIds){
            Employee employee = _employeeRepositpry.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: "+employeeId));
            project.getEmployeeSet().add(employee);
        }
        _projectRepository.save(project);
    }

    @Transactional
    public void deleteProjectById(Long id) throws ProjectNotFoundException {
        Project project = _projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id));

        project.setEmployeeSet(null);
        _projectRepository.save(project);
        _projectRepository.delete(project);
    }

    @Transactional
    public void deleteProjectByName(String name) throws  ProjectNotFoundException{
        Project project = _projectRepository.findByName(name)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with name: "+ name));

        project.setEmployeeSet(null);
        _projectRepository.save(project);
        _projectRepository.delete(project);
    }

    public Long getProjectBudgetForDepartment(String name) throws DepartmentNotFoundException {
        Department department = _departmentRepository.findByName(name)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + name));

        List<Project> projectList = _projectRepository.findAllByDepartment(department);
        Long totalBudget = 0L;
        for(Project project : projectList){
            totalBudget += project.getBudget();
        }
        return totalBudget;
    }

    public void removeEmployeesToProject(Long id, List<Long> employeeIds)  throws ProjectNotFoundException, EmployeeNotFoundException{
        Project project = _projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: "+ id));

        for(Long employeeId: employeeIds){
            Employee employee = _employeeRepositpry.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: "+employeeId));
            project.getEmployeeSet().remove(employee);
        }
        _projectRepository.save(project);
    }
}
