package com.airtribe.EmployeeManagementSystem.Service;

import com.airtribe.EmployeeManagementSystem.Config.TestSecurityConfig;
import com.airtribe.EmployeeManagementSystem.DTOs.DepartmentDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import com.airtribe.EmployeeManagementSystem.Utility.DepartmentEntityMapper;
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
import static org.mockito.Mockito.times;

@SpringBootTest
@Import(TestSecurityConfig.class)
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService _departmentService;

    @MockBean
    private DepartmentRepository _departmentRepository;

    @MockBean
    private EmployeeRepository _employeeRepository;

    @MockBean
    private ProjectRepository _projectRepository;

    @MockBean
    private DepartmentEntityMapper _departmentEntityMapper;

    private Department cascadeDepartment;

    private DepartmentDTO cascadeDepartmentDTO;

    private Department apolloDepartment;

    private DepartmentDTO apolloDepartmentDTO;

    private List<Department> departmentList;

    private List<DepartmentDTO> departmentDTOS;

    private Project project;

    private  Employee manager;

    private Employee employee;

    private List<Employee> employeeList;

    private String managerEmail;

    private Long id;

    private String name;

    private String location;

    private Map<String,String> change;

    @BeforeEach()
    public void setUp(){
        id = 1L;
        name = "Cascade";
        managerEmail = "srini.a@gmail.com";
        location = "DLF 1-A";

        manager = new Employee("Srini Amarnathan","Manager","srini.a@gmail.com");
        employee = new Employee("Priyadharshini B","Employee","priya.a@gmail.com");

        cascadeDepartmentDTO = new DepartmentDTO("Cascade","Manages Asset","DLF 1-A",managerEmail);
        cascadeDepartment = new Department("Cascade","Manages Asset","DLF 1-A", manager);

        apolloDepartmentDTO = new DepartmentDTO("Apollo","Manages Event","DLF 1-A",managerEmail);
        apolloDepartment = new Department("Apollo","Manages Event","DLF 1-A", manager);

        departmentList = new ArrayList<>();
        departmentList.add(cascadeDepartment);
        departmentList.add(apolloDepartment);

        departmentDTOS = new ArrayList<>();
        departmentDTOS.add(cascadeDepartmentDTO);
        departmentDTOS.add(apolloDepartmentDTO);

        employeeList = new ArrayList<>();
        employeeList.add(manager);
        employeeList.add(employee);

        project = new Project("SSS", "Self Service",2000L,employee,cascadeDepartment);

        change = new HashMap<>();
        change.put("description", apolloDepartment.getDescription());
        change.put("location", apolloDepartment.getLocation());
    }

    public DepartmentServiceTest(){

    }

    @Test
    void addDepartments_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(cascadeDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentEntityMapper.toEntity(cascadeDepartmentDTO)).thenReturn(cascadeDepartment);
        when(_departmentRepository.save(cascadeDepartment)).thenReturn(null);
        when(_employeeRepository.save(manager)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _departmentService.addDepartments(cascadeDepartmentDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(cascadeDepartmentDTO.getManagerEmail());
        verify(_departmentEntityMapper, times(1)).toEntity(cascadeDepartmentDTO);
        verify(_departmentRepository, times(1)).findByManager(manager);
        verify(_departmentRepository, times(1)).save(cascadeDepartment);
        verify(_employeeRepository, times(1)).save(manager);
        Assertions.assertEquals(cascadeDepartmentDTO.getName(),cascadeDepartment.getName());
        Assertions.assertEquals(cascadeDepartmentDTO.getDescription(),cascadeDepartment.getDescription());
        Assertions.assertEquals(cascadeDepartmentDTO.getLocation(),cascadeDepartment.getLocation());
        Assertions.assertEquals(cascadeDepartmentDTO.getManagerEmail(),cascadeDepartment.getManager().getEmail());
    }

    @Test
    void addDepartments_ThrowsRunTimeException(){
        // Set Up
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentEntityMapper.toEntity(cascadeDepartmentDTO)).thenReturn(cascadeDepartment);
        when(_departmentRepository.save(cascadeDepartment)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _departmentService.addDepartments(cascadeDepartmentDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(cascadeDepartmentDTO.getManagerEmail());
        verify(_departmentEntityMapper, times(1)).toEntity(cascadeDepartmentDTO);
        verify(_departmentRepository, times(1)).findByManager(manager);
        verify(_departmentRepository, times(1)).save(cascadeDepartment);
    }

    @Test
    void addDepartments_ThrowsManagerRequirementException1(){
        // Set Up
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.of(cascadeDepartment));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,() -> _departmentService.addDepartments(cascadeDepartmentDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(cascadeDepartmentDTO.getManagerEmail());
        verify(_departmentRepository, times(1)).findByManager(manager);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void addDepartments_ThrowsManagerRequirementException2(){
        // Set Up
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(employee));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,() -> _departmentService.addDepartments(cascadeDepartmentDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(cascadeDepartmentDTO.getManagerEmail());
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void addDepartments_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _departmentService.addDepartments(cascadeDepartmentDTO));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(cascadeDepartmentDTO.getManagerEmail());
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartments_Successfully(){
        // Set Up
        when(_departmentRepository.findAll()).thenReturn(departmentList);
        when(_departmentEntityMapper.toDTOList(departmentList)).thenReturn(departmentDTOS);
        // Act
        List<DepartmentDTO> departmentDTOList = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartments());
        // Asset
        verify(_departmentRepository, times(1)).findAll();
        verify(_departmentEntityMapper, times(1)).toDTOList(departmentList);
        Assertions.assertEquals(departmentDTOS.size(),departmentDTOList.size());
    }

    @Test
    void getDepartments_ThrowsNoRecordFoundException(){
        // Set Up
        when(_departmentRepository.findAll()).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _departmentService.getDepartments());
        // Assert
        verify(_departmentRepository, times(1)).findAll();
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartmentById_Successfully(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentEntityMapper.toDTO(cascadeDepartment)).thenReturn(cascadeDepartmentDTO);
        // Act
        DepartmentDTO departmentDTO = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartmentById(id));
        // Asset
        verify(_departmentRepository,times(1)).findById(id);
        verify(_departmentEntityMapper,times(1)).toDTO(cascadeDepartment);
        Assertions.assertEquals(cascadeDepartmentDTO.getName(), departmentDTO.getName());
        Assertions.assertEquals(cascadeDepartmentDTO.getDescription(), departmentDTO.getDescription());
        Assertions.assertEquals(cascadeDepartmentDTO.getLocation(), departmentDTO.getLocation());
        Assertions.assertEquals(cascadeDepartmentDTO.getManagerEmail(), departmentDTO.getManagerEmail());
    }

    @Test
    void getDepartmentById_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class, () -> _departmentService.getDepartmentById(id));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
    }

    @Test
    void getDepartmentByName_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentEntityMapper.toDTO(cascadeDepartment)).thenReturn(cascadeDepartmentDTO);
        // Act
        DepartmentDTO departmentDTO = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartmentByName(name));
        // Asset
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentEntityMapper,times(1)).toDTO(cascadeDepartment);
        Assertions.assertEquals(cascadeDepartmentDTO.getName(), departmentDTO.getName());
        Assertions.assertEquals(cascadeDepartmentDTO.getDescription(), departmentDTO.getDescription());
        Assertions.assertEquals(cascadeDepartmentDTO.getLocation(), departmentDTO.getLocation());
        Assertions.assertEquals(cascadeDepartmentDTO.getManagerEmail(), departmentDTO.getManagerEmail());
    }

    @Test
    void getDepartmentByName_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class, () -> _departmentService.getDepartmentByName(name));
        // Assert
        verify(_departmentRepository,times(1)).findByName(name);
    }

    @Test
    void getDepartmentByManager_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentEntityMapper.toDTO(cascadeDepartment)).thenReturn(cascadeDepartmentDTO);
        // Act
        DepartmentDTO departmentDTO = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartmentByManager(managerEmail));
        // Asset
        verify(_employeeRepository, times(1)).findByEmail(managerEmail);
        verify(_departmentRepository, times(1)).findByManager(manager);
        verify(_departmentEntityMapper, times(1)).toDTO(cascadeDepartment);
        Assertions.assertEquals(cascadeDepartmentDTO.getName(), departmentDTO.getName());
        Assertions.assertEquals(cascadeDepartmentDTO.getDescription(), departmentDTO.getDescription());
        Assertions.assertEquals(cascadeDepartmentDTO.getLocation(), departmentDTO.getLocation());
        Assertions.assertEquals(cascadeDepartmentDTO.getManagerEmail(), departmentDTO.getManagerEmail());
    }

    @Test
    void getDepartmentByManager_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _departmentService.getDepartmentByManager(managerEmail));
        // Asset
        verify(_employeeRepository, times(1)).findByEmail(managerEmail);
        verify(_departmentRepository, times(1)).findByManager(manager);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartmentByManager_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _departmentService.getDepartmentByManager(managerEmail));
        // Asset
        verify(_employeeRepository, times(1)).findByEmail(managerEmail);
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartmentByLocation_Successfully(){
        // Set Up
        when(_departmentRepository.findAllByLocation(location)).thenReturn(departmentList);
        when(_departmentEntityMapper.toDTOList(departmentList)).thenReturn(departmentDTOS);
        // Act
        List<DepartmentDTO> departmentDTO = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartmentByLocation(location));
        // Asset
        verify(_departmentRepository,times(1)).findAllByLocation(location);
        verify(_departmentEntityMapper,times(1)).toDTOList(departmentList);
        Assertions.assertEquals(departmentDTOS.size(), departmentDTO.size());
    }

    @Test
    void getDepartmentByLocation_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findAllByLocation(location)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _departmentService.getDepartmentByLocation(location));
        // Asset
        verify(_departmentRepository,times(1)).findAllByLocation(location);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartmentsByProject_Successfully(){
        // Set Up
        when(_projectRepository.findByName(project.getName())).thenReturn(Optional.of(project));
        when(_departmentRepository.findAllByProject(project)).thenReturn(departmentList);
        when(_departmentEntityMapper.toDTOList(departmentList)).thenReturn(departmentDTOS);
        // Act
        List<DepartmentDTO> departmentDTO = Assertions.assertDoesNotThrow(() -> _departmentService.getDepartmentsByProject(project.getName()));
        // Assert
        verify(_projectRepository,times(1)).findByName(project.getName());
        verify(_departmentRepository,times(1)).findAllByProject(project);
        verify(_departmentEntityMapper,times(1)).toDTOList(departmentList);
        Assertions.assertEquals(departmentDTOS.size(), departmentDTO.size());
    }

    @Test
    void getDepartmentsByProject_ThrowsNoRecordFoundException(){
        // Set Up
        when(_projectRepository.findByName(project.getName())).thenReturn(Optional.of(project));
        when(_departmentRepository.findAllByProject(project)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class,() -> _departmentService.getDepartmentsByProject(project.getName()));
        // Assert
        verify(_projectRepository,times(1)).findByName(project.getName());
        verify(_departmentRepository,times(1)).findAllByProject(project);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void getDepartmentsByProject_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findByName(project.getName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,() -> _departmentService.getDepartmentsByProject(project.getName()));
        // Assert
        verify(_projectRepository,times(1)).findByName(project.getName());
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_departmentEntityMapper);
    }

    @Test
    void updateDepartmentAllDetails_Successfully(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.save(cascadeDepartment)).thenReturn(null);
        when(_employeeRepository.save(manager)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
        verify(_employeeRepository,times(1)).save(manager);
        Assertions.assertEquals(apolloDepartment.getName(),cascadeDepartment.getName());
        Assertions.assertEquals(apolloDepartment.getLocation(),cascadeDepartment.getLocation());
        Assertions.assertEquals(apolloDepartment.getDescription(),cascadeDepartment.getDescription());
        Assertions.assertEquals(apolloDepartment.getManager().getName(),cascadeDepartment.getManager().getName());
    }

    @Test
    void updateDepartmentAllDetails_ThrowsRunTimeException1(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.save(cascadeDepartment)).thenReturn(null);
        when(_employeeRepository.save(manager)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
        verify(_employeeRepository,times(1)).save(manager);
    }

    @Test
    void updateDepartmentAllDetails_ThrowsRunTimeException2(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.save(cascadeDepartment)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
    }

    @Test
    void updateDepartmentAllDetails_ThrowsManagerRequirementException1(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.of(cascadeDepartment));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
        verify(_departmentRepository,times(1)).findByManager(manager);
    }

    @Test
    void updateDepartmentAllDetails_ThrowsManagerRequirementException2(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.of(employee));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
    }

    @Test
    void updateDepartmentAllDetails_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findByEmail(apolloDepartmentDTO.getManagerEmail())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_employeeRepository,times(1)).findByEmail(apolloDepartmentDTO.getManagerEmail());
    }

    @Test
    void updateDepartmentAllDetails_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,()-> _departmentService.updateDepartmentAllDetails(id,apolloDepartmentDTO));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void updateDepartmentName_Successfully(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentRepository.save(cascadeDepartment)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(()-> _departmentService.updateDepartmentName(id,name));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
        Assertions.assertEquals(cascadeDepartment.getName(),name);
    }

    @Test
    void updateDepartmentName_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentRepository.save(cascadeDepartment)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,()-> _departmentService.updateDepartmentName(id,name));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
    }

    @Test
    void updateDepartmentName_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,()-> _departmentService.updateDepartmentName(id,name));
        // Assert
        verify(_departmentRepository,times(1)).findById(id);
    }

    @Test
    void updateDepartmentData_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentRepository.save(cascadeDepartment)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(() -> _departmentService.updateDepartmentData(name,change));
        // Assert
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);

        for (Map.Entry<String, String> entry : change.entrySet()) {
            switch (entry.getKey()){
                case "description":
                    Assertions.assertEquals(entry.getValue(), cascadeDepartment.getDescription());
                    break;
                case "location":
                    Assertions.assertEquals(entry.getValue(), cascadeDepartment.getLocation());
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    void updateDepartmentData_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(cascadeDepartment));
        when(_departmentRepository.save(cascadeDepartment)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _departmentService.updateDepartmentData(name,change));
        // Assert
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentRepository,times(1)).save(cascadeDepartment);
    }

    @Test
    void updateDepartmentData_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _departmentService.updateDepartmentData(name,change));
        // Assert
        verify(_departmentRepository,times(1)).findByName(name);
    }

    @Test
    void updateDepartmentManager_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(apolloDepartment));
        when(_departmentRepository.save(apolloDepartment)).thenReturn(null);
        when(_employeeRepository.save(manager)).thenReturn(null);
        // Act
        Assertions.assertDoesNotThrow(()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentRepository,times(1)).save(apolloDepartment);
        verify(_employeeRepository,times(1)).save(manager);
        Assertions.assertEquals(managerEmail,apolloDepartment.getManager().getEmail());
    }

    @Test
    void updateDepartmentManager_ThrowsRunTimeException1(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(apolloDepartment));
        when(_departmentRepository.save(apolloDepartment)).thenReturn(null);
        when(_employeeRepository.save(manager)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentRepository,times(1)).save(apolloDepartment);
        verify(_employeeRepository,times(1)).save(manager);
    }

    @Test
    void updateDepartmentManager_ThrowsRunTimeException2(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(apolloDepartment));
        when(_departmentRepository.save(apolloDepartment)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).findByName(name);
        verify(_departmentRepository,times(1)).save(apolloDepartment);
    }

    @Test
    void updateDepartmentManager_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.empty());
        when(_departmentRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
        verify(_departmentRepository,times(1)).findByManager(manager);
        verify(_departmentRepository,times(1)).findByName(name);
    }

    @Test
    void updateDepartmentManager_ThrowsManagerRequirementException1(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(_departmentRepository.findByManager(manager)).thenReturn(Optional.of(cascadeDepartment));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
        verify(_departmentRepository,times(1)).findByManager(manager);
    }

    @Test
    void updateDepartmentManager_ThrowsManagerRequirementException2(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(employee));
        // Act
        Assertions.assertThrows(ManagerRequirementException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
    }

    @Test
    void updateDepartmentManager_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,()-> _departmentService.updateDepartmentManager(name,managerEmail));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(managerEmail);
    }

    @Test
    void deleteDepartmentById_Successfully(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findAllByDepartment(cascadeDepartment)).thenReturn(employeeList);
        when(_employeeRepository.saveAll(employeeList)).thenReturn(null);
        doNothing().when(_departmentRepository).delete(cascadeDepartment);
        // Act
        Assertions.assertDoesNotThrow(() -> _departmentService.deleteDepartmentById(id));
        // Assert
        verify(_departmentRepository, times(1)).findById(id);
        verify(_employeeRepository, times(1)).findAllByDepartment(cascadeDepartment);
        verify(_employeeRepository, times(1)).saveAll(employeeList);
        verify(_departmentRepository, times(1)).delete(cascadeDepartment);
        for(Employee emp: employeeList){
            Assertions.assertNull(emp.getDepartment());
        }
    }

    @Test
    void deleteDepartmentById_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findAllByDepartment(cascadeDepartment)).thenReturn(employeeList);
        when(_employeeRepository.saveAll(employeeList)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _departmentService.deleteDepartmentById(id));
        // Assert
        verify(_departmentRepository, times(1)).findById(id);
        verify(_employeeRepository, times(1)).findAllByDepartment(cascadeDepartment);
        verify(_employeeRepository, times(1)).saveAll(employeeList);
    }

    @Test
    void deleteDepartmentById_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _departmentService.deleteDepartmentById(id));
        // Assert
        verify(_departmentRepository, times(1)).findById(id);
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void deleteDepartmentByName_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findAllByDepartment(cascadeDepartment)).thenReturn(employeeList);
        when(_employeeRepository.saveAll(employeeList)).thenReturn(null);
        doNothing().when(_departmentRepository).delete(cascadeDepartment);
        // Act
        Assertions.assertDoesNotThrow(() -> _departmentService.deleteDepartmentByName(name));
        // Assert
        verify(_departmentRepository, times(1)).findByName(name);
        verify(_employeeRepository, times(1)).findAllByDepartment(cascadeDepartment);
        verify(_employeeRepository, times(1)).saveAll(employeeList);
        verify(_departmentRepository, times(1)).delete(cascadeDepartment);
        for(Employee emp: employeeList){
            Assertions.assertNull(emp.getDepartment());
        }
    }

    @Test
    void deleteDepartmentByName_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.of(cascadeDepartment));
        when(_employeeRepository.findAllByDepartment(cascadeDepartment)).thenReturn(employeeList);
        when(_employeeRepository.saveAll(employeeList)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _departmentService.deleteDepartmentByName(name));
        // Assert
        verify(_departmentRepository, times(1)).findByName(name);
        verify(_employeeRepository, times(1)).findAllByDepartment(cascadeDepartment);
        verify(_employeeRepository, times(1)).saveAll(employeeList);
    }

    @Test
    void deleteDepartmentByName_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(name)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _departmentService.deleteDepartmentByName(name));
        // Assert
        verify(_departmentRepository, times(1)).findByName(name);
        verifyNoInteractions(_employeeRepository);
    }
}
