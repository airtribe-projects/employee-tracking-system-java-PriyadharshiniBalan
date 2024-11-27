package com.airtribe.EmployeeManagementSystem.Service;

import com.airtribe.EmployeeManagementSystem.Config.TestSecurityConfig;
import com.airtribe.EmployeeManagementSystem.DTOs.ProjectDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.DepartmentNotFoundException;
import com.airtribe.EmployeeManagementSystem.Exception.EmployeeNotFoundException;
import com.airtribe.EmployeeManagementSystem.Exception.NoRecordFoundException;
import com.airtribe.EmployeeManagementSystem.Exception.ProjectNotFoundException;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import com.airtribe.EmployeeManagementSystem.Utility.ProjectEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class ProjectServiceTest {

    @MockBean
    private ProjectRepository _projectRepository;

    @MockBean
    private EmployeeRepository _employeeRepository;

    @MockBean
    private DepartmentRepository _departmentRepository;

    @MockBean
    private ProjectEntityMapper _projectEntityMapper;

    @Autowired
    private ProjectService _projectService;

    private Project project;

    private ProjectDTO projectDTO;

    private ProjectDTO updatedDetails;

    private List<Project> projectList;

    private List<ProjectDTO> projectDTOS;

    private Employee lead;

    private Employee manager;

    private List<Long> employeeList;

    private Department department;

    private Long id;

    private String leadEmail;

    private String name;

    private Map<String,String> change;

    @BeforeEach
    public void setUp(){
        id = 1L;
        leadEmail = "priya.a@gmail.com";
        name = "Priyadharshini B";

        manager = new Employee("Srini","Manager","srini.a@gmail.com");
        department = new Department("Cascade","Manages Asset", "DLF 1-A",manager);
        manager.setDepartment(department);
        lead = new Employee("Priyadharshini B","Employee","priya.a@gmail.com");
        lead.setDepartment(department);

        project = new Project("AAA","Test Service",2000L,lead,department);
        projectDTO = new ProjectDTO("AAA","Test Service",2000L,"priya.a@gmail.com","Cascade");

        updatedDetails = new ProjectDTO("MB","Multi Test",5000L,leadEmail,"Cascade");

        projectList = new ArrayList<>();
        projectList.add(project);
        projectList.add(new Project("MB","Multi State",5000L,manager,department));

        projectDTOS = new ArrayList<>();
        projectDTOS.add(projectDTO);
        projectDTOS.add(new ProjectDTO("MB","Multi State",5000L,"srini.a@gmail.com","Cascade"));

        change = new HashMap<>();
        change.put("description",updatedDetails.getDescription());
        change.put("budget",updatedDetails.getBudget().toString());

        employeeList = new ArrayList<>();
        employeeList.add(lead.getEmployeeId());
        employeeList.add(manager.getEmployeeId());
    }

    public ProjectServiceTest(){

    }

    @Test
    void addProjects_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(projectDTO.getLeadEmail())).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(projectDTO.getDepartmentName())).thenReturn(Optional.of(department));
        when(_projectEntityMapper.toEntity(projectDTO)).thenReturn(project);
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _projectService.addProjects(projectDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(projectDTO.getLeadEmail());
        verify(_departmentRepository, times(1)).findByName(projectDTO.getDepartmentName());
        verify(_projectEntityMapper, times(1)).toEntity(projectDTO);
        verify(_projectRepository, times(1)).save(project);
        Assertions.assertEquals(projectDTO.getName(),project.getName());
        Assertions.assertEquals(projectDTO.getDescription(),project.getDescription());
        Assertions.assertEquals(projectDTO.getBudget(),project.getBudget());
        Assertions.assertEquals(projectDTO.getLeadEmail(),project.getLead().getEmail());
        Assertions.assertEquals(projectDTO.getDepartmentName(),project.getDepartment().getName());
    }

    @Test
    void addProjects_ThrowsRunTimeException(){
        // Set Up
        when(_employeeRepository.findByEmail(projectDTO.getLeadEmail())).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(projectDTO.getDepartmentName())).thenReturn(Optional.of(department));
        when(_projectEntityMapper.toEntity(projectDTO)).thenReturn(project);
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.addProjects(projectDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(projectDTO.getLeadEmail());
        verify(_departmentRepository, times(1)).findByName(projectDTO.getDepartmentName());
        verify(_projectEntityMapper, times(1)).toEntity(projectDTO);
        verify(_projectRepository, times(1)).save(project);
    }

    @Test
    void addProjects_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(projectDTO.getLeadEmail())).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(projectDTO.getDepartmentName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _projectService.addProjects(projectDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(projectDTO.getLeadEmail());
        verify(_departmentRepository, times(1)).findByName(projectDTO.getDepartmentName());
        verifyNoInteractions(_projectEntityMapper);
        verifyNoInteractions(_projectRepository);
    }

    @Test
    void addProjects_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(projectDTO.getLeadEmail())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.addProjects(projectDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(projectDTO.getLeadEmail());
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_projectEntityMapper);
        verifyNoInteractions(_projectRepository);
    }

    @Test
    void getProjects_Successfully(){
        // Set Up
        when(_projectRepository.findAll()).thenReturn(projectList);
        when(_projectEntityMapper.toDTOList(projectList)).thenReturn(projectDTOS);
        // Act
        List<ProjectDTO> projectDTOList = Assertions.assertDoesNotThrow(() -> _projectService.getProjects());
        // Asset
        verify(_projectRepository, times(1)).findAll();
        verify(_projectEntityMapper, times(1)).toDTOList(projectList);
        Assertions.assertEquals(projectDTOList.size(),projectDTOS.size());
    }

    @Test
    void getProjects_ThrowsNoRecordFoundException(){
        // Set Up
        when(_projectRepository.findAll()).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _projectService.getProjects());
        // Assert
        verify(_projectRepository, times(1)).findAll();
        verifyNoInteractions(_projectEntityMapper);
    }

    @Test
    void getProjectById_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_projectEntityMapper.toDTO(project)).thenReturn(projectDTO);
        // Act
        ProjectDTO dbProject = Assertions.assertDoesNotThrow(() -> _projectService.getProjectById(id));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_projectEntityMapper,times(1)).toDTO(project);
        Assertions.assertEquals(projectDTO.getName(),dbProject.getName());
        Assertions.assertEquals(projectDTO.getDescription(),dbProject.getDescription());
        Assertions.assertEquals(projectDTO.getBudget(),dbProject.getBudget());
        Assertions.assertEquals(projectDTO.getLeadEmail(),dbProject.getLeadEmail());
        Assertions.assertEquals(projectDTO.getDepartmentName(),dbProject.getDepartmentName());
    }

    @Test
    void getProjectById_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class, () -> _projectService.getProjectById(id));
        // Assert
        verify(_projectRepository,times(1)).findById(id);
    }

    @Test
    void getProjectByName_Successfully(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectEntityMapper.toDTO(project)).thenReturn(projectDTO);
        // Act
        ProjectDTO dbProject = Assertions.assertDoesNotThrow(() -> _projectService.getProjectByName(name));
        // Asset
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectEntityMapper,times(1)).toDTO(project);
        Assertions.assertEquals(projectDTO.getName(),dbProject.getName());
        Assertions.assertEquals(projectDTO.getDescription(),dbProject.getDescription());
        Assertions.assertEquals(projectDTO.getBudget(),dbProject.getBudget());
        Assertions.assertEquals(projectDTO.getLeadEmail(),dbProject.getLeadEmail());
        Assertions.assertEquals(projectDTO.getDepartmentName(),dbProject.getDepartmentName());
    }

    @Test
    void getProjectByName_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class, () -> _projectService.getProjectByName(name));
        // Assert
        verify(_projectRepository,times(1)).findByName(name);
    }

    @Test
    void getProjectByLead_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_projectRepository.findByLead(lead)).thenReturn(Optional.of(project));
        when(_projectEntityMapper.toDTO(project)).thenReturn(projectDTO);
        // Act
        ProjectDTO dbProject = Assertions.assertDoesNotThrow(() -> _projectService.getProjectByLead(leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_projectRepository,times(1)).findByLead(lead);
        verify(_projectEntityMapper,times(1)).toDTO(project);
        Assertions.assertEquals(projectDTO.getName(),dbProject.getName());
        Assertions.assertEquals(projectDTO.getDescription(),dbProject.getDescription());
        Assertions.assertEquals(projectDTO.getBudget(),dbProject.getBudget());
        Assertions.assertEquals(projectDTO.getLeadEmail(),dbProject.getLeadEmail());
        Assertions.assertEquals(projectDTO.getDepartmentName(),dbProject.getDepartmentName());
    }

    @Test
    void getProjectByLead_ThrowsProjectNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_projectRepository.findByLead(lead)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.getProjectByLead(leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_projectRepository,times(1)).findByLead(lead);
        verifyNoInteractions(_projectEntityMapper);
    }

    @Test
    void getProjectByLead_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.getProjectByLead(leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verifyNoInteractions(_projectRepository);
        verifyNoInteractions(_projectEntityMapper);

    }

    @Test
    void getProjectsByDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.findAllByDepartment(department)).thenReturn(projectList);
        when(_projectEntityMapper.toDTOList(projectList)).thenReturn(projectDTOS);
        // Act
        List<ProjectDTO> dbProjects = Assertions.assertDoesNotThrow(() -> _projectService.getProjectsByDepartment(department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).findAllByDepartment(department);
        verify(_projectEntityMapper,times(1)).toDTOList(projectList);
        Assertions.assertEquals(dbProjects.size(),projectDTOS.size());
    }

    @Test
    void getProjectsByDepartment_ThrowsNoRecordFoundException(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.findAllByDepartment(department)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class,() -> _projectService.getProjectsByDepartment(department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).findAllByDepartment(department);
        verifyNoInteractions(_projectEntityMapper);
    }

    @Test
    void getProjectsByDepartment_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _projectService.getProjectsByDepartment(department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verifyNoInteractions(_projectRepository);
        verifyNoInteractions(_projectEntityMapper);
    }

    @Test
    void updateProjectAllDetails_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.updateProjectAllDetails(id,projectDTO));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectAllDetails_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.updateProjectAllDetails(id,projectDTO));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectAllDetails_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _projectService.updateProjectAllDetails(id,projectDTO));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_departmentRepository,times(1)).findByName(department.getName());
    }

    @Test
    void updateProjectAllDetails_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.updateProjectAllDetails(id,projectDTO));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verifyNoInteractions(_departmentRepository);
    }

    @Test
    void updateProjectAllDetails_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.updateProjectAllDetails(id,projectDTO));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verifyNoInteractions(_employeeRepository);
        verifyNoInteractions(_departmentRepository);
    }

    @Test
    void updateProjectName_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.updateProjectName(id,name));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectName_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.updateProjectName(id,name));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectName_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.updateProjectName(id,name));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
    }

    @Test
    void updateProjectData_Successfully(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _projectService.updateProjectData(name, change));
        // Assert
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository, times(1)).save(project);

        for (Map.Entry<String, String> entry : change.entrySet()) {
            switch (entry.getKey()){
                case "description":
                    Assertions.assertEquals(entry.getValue(), project.getDescription());
                    break;
                case "budget":
                    Assertions.assertEquals(Long.parseLong(entry.getValue()), project.getBudget());
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    void updateProjectData_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.updateProjectData(name, change));
        // Assert
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository, times(1)).save(project);
    }

    @Test
    void updateProjectData_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.updateProjectData(name, change));
        // Assert
        verify(_projectRepository,times(1)).findByName(name);
    }

    @Test
    void updateProjectLead_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.updateProjectLead(name,leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectLead_ThrowsRunTimeException(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.updateProjectLead(name, leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectLead_ThrowsProjectNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.of(lead));
        when(_projectRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.updateProjectLead(name,leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verify(_projectRepository,times(1)).findByName(name);
    }

    @Test
    void updateProjectLead_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(leadEmail)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.updateProjectLead(name,leadEmail));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(leadEmail);
        verifyNoInteractions(_projectRepository);
    }

    @Test
    void updateProjectDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.updateProjectDepartment(name,department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectDepartment_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.updateProjectDepartment(name,department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).findByName(name);
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void updateProjectDepartment_ThrowsProjectNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));
        when(_projectRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.updateProjectDepartment(name,department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verify(_projectRepository,times(1)).findByName(name);
    }

    @Test
    void updateProjectDepartment_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(department.getName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _projectService.updateProjectDepartment(name,department.getName()));
        // Asset
        verify(_departmentRepository,times(1)).findByName(department.getName());
        verifyNoInteractions(_projectRepository);
    }

    @Test
    void addEmployeesToProject_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.of(lead));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.addEmployeesToProject(id,employeeList));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(employeeList.size())).findById(lead.getEmployeeId());
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void addEmployeesToProject_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.of(lead));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.addEmployeesToProject(id,employeeList));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(employeeList.size())).findById(lead.getEmployeeId());
        verify(_projectRepository,times(1)).save(project);
    }

    @Test
    void addEmployeesToProject_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.addEmployeesToProject(id,employeeList));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findById(lead.getEmployeeId());
    }

    @Test
    void addEmployeesToProject_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.addEmployeesToProject(id,employeeList));
        // Asset
        verify(_projectRepository,times(1)).findById(id);
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void deleteProjectById_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        doNothing().when(_projectRepository).delete(project);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.deleteProjectById(id));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
        verify(_projectRepository, times(1)).save(project);
        verify(_projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProjectById_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.deleteProjectById(id));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
        verify(_projectRepository, times(1)).save(project);
    }

    @Test
    void deleteProjectById_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.deleteProjectById(id));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
    }

    @Test
    void deleteProjectByName_Successfully(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenReturn(null);
        doNothing().when(_projectRepository).delete(project);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.deleteProjectByName(name));
        // Assert
        verify(_projectRepository, times(1)).findByName(name);
        verify(_projectRepository, times(1)).save(project);
        verify(_projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProjectByName_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.of(project));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.deleteProjectByName(name));
        // Assert
        verify(_projectRepository, times(1)).findByName(name);
        verify(_projectRepository, times(1)).save(project);
    }

    @Test
    void deleteProjectByName_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.deleteProjectByName(name));
        // Assert
        verify(_projectRepository, times(1)).findByName(name);
    }

    @Test
    void getProjectBudgetForDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(department));
        when(_projectRepository.findAllByDepartment(department)).thenReturn(projectList);
        // Act
        Long budget = Assertions.assertDoesNotThrow(() -> _projectService.getProjectBudgetForDepartment(name));
        // Assert
        verify(_departmentRepository, times(1)).findByName(name);
        verify(_projectRepository, times(1)).findAllByDepartment(department);
        Assertions.assertEquals(budget, 7000);
    }

    @Test
    void getProjectBudgetForDepartment_ThrowsDepartmentNotFound(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _projectService.getProjectBudgetForDepartment(name));
        // Assert
        verify(_departmentRepository, times(1)).findByName(name);
    }

    @Test
    void removeEmployeesToProject_Successfully(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.of(lead));
        when(_projectRepository.save(project)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _projectService.removeEmployeesToProject(id,employeeList));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
        verify(_employeeRepository, times(employeeList.size())).findById(lead.getEmployeeId());
        verify(_projectRepository, times(1)).save(project);
    }

    @Test
    void removeEmployeesToProject_ThrowsRunTimeException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.of(lead));
        when(_projectRepository.save(project)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _projectService.removeEmployeesToProject(id,employeeList));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
        verify(_employeeRepository, times(employeeList.size())).findById(lead.getEmployeeId());
        verify(_projectRepository, times(1)).save(project);

    }

    @Test
    void removeEmployeesToProject_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(_employeeRepository.findById(lead.getEmployeeId())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _projectService.removeEmployeesToProject(id,employeeList));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
    }

    @Test
    void removeEmployeesToProject_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _projectService.removeEmployeesToProject(id,employeeList));
        // Assert
        verify(_projectRepository, times(1)).findById(id);
    }
}
