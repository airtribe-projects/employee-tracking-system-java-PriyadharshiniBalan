package com.airtribe.EmployeeManagementSystem.Service;

import com.airtribe.EmployeeManagementSystem.Config.TestSecurityConfig;
import com.airtribe.EmployeeManagementSystem.DTOs.EmployeeDTO;
import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import com.airtribe.EmployeeManagementSystem.Exception.*;
import com.airtribe.EmployeeManagementSystem.Repository.DepartmentRepository;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import com.airtribe.EmployeeManagementSystem.Repository.ProjectRepository;
import com.airtribe.EmployeeManagementSystem.Utility.EmployeeEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
//@ContextConfiguration(classes = {TestSecurityConfig.class})
public class EmployeeServiceTest {

    @MockBean
    private EmployeeRepository _employeeRepository;

    @MockBean
    private DepartmentRepository _departmentRepository;

    @MockBean
    private ProjectRepository _projectRepository;

    @MockBean
    private EmployeeEntityMapper _employeeEntityMapper;

    @Autowired
    private EmployeeService _employeeService;

    private Department CascadeObj;

    private Department ApolloObj;

    private EmployeeDTO employeeDTO;

    private Employee employee;

    private Employee manager_cascade;

    private EmployeeDTO updatedDetails;

    private List<Employee> employeeList;

    private List<EmployeeDTO> employeeDTOS;

    private Long id;

    private String email;

    private String name;

    private String designation;

    private String Cascade;

    private Map<String,String> change;

    private String projectName;

    private Project ProjectObj;

    @BeforeEach()
    public void setUp(){
        id = 1L;
        email = "priya.a@gmail.com";
        name = "Priyadharshini B";
        designation = "Employee";
        Cascade = "Cascade";
        String apollo = "Apollo";
        projectName = "Seller Self Service";

        manager_cascade = new Employee("Srini Amarnathan","Manager","srini.a@gmail.com");
        Employee manager_apollo = new Employee("Kavin Franco", "Manager", "kavin.a@gmail.com");

        CascadeObj =  new Department(Cascade,"Manages Asset","DLF",manager_cascade);
        ApolloObj = new Department(apollo, "Manges Events", "DLF", manager_apollo);

        manager_cascade.setDepartment(CascadeObj);
        manager_apollo.setDepartment(ApolloObj);

        employeeDTO = new EmployeeDTO(name, designation,email, Cascade);
        employee = new Employee(name,designation,email);

        employeeList = new ArrayList<>();
        employeeList.add(new Employee(name,designation,"priya.ab@gmail.com"));
        employeeList.add(employee);

        employeeDTOS = new ArrayList<>();
        employeeDTOS.add(new EmployeeDTO(name, designation, "priya.ab@gmail.com", Cascade));
        employeeDTOS.add(employeeDTO);

        updatedDetails = new EmployeeDTO("Nagarajan",designation,"nagaraj.a@gmail.com", apollo);

        change = new HashMap<>();
        change.put("name",updatedDetails.getName());
        change.put("designation",updatedDetails.getDesignation());

        ProjectObj = new Project("Seller Self Service", "Self service", 20000L, employee, CascadeObj);
    }

    public EmployeeServiceTest(){

    }

    @Test
    void addEmployeeWithoutDepartment_Successfully(){
        // Set Up
        employeeDTO.setDepartmentName(null);
        when(_employeeEntityMapper.toEntity(employeeDTO)).thenReturn(employee);
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.addEmployees(employeeDTO));
        // Assert
        verifyNoInteractions(_departmentRepository);
        verify(_employeeEntityMapper, times(1)).toEntity(employeeDTO);
        verify(_employeeRepository, times(1)).save(employee);
        Assertions.assertEquals(employeeDTO.getName(),employee.getName());
        Assertions.assertEquals(employeeDTO.getDesignation(),employee.getDesignation());
        Assertions.assertEquals(employeeDTO.getEmail(),employee.getEmail());
        //Assertions.assertEquals(employeeDTO.getPassword(),employee.getPassword());
        assertNull(employee.getDepartment());
    }

    @Test
    void addEmployee_ThrowsRunTimeException(){
        // Set Up
        employeeDTO.setDepartmentName(null);
        when(_employeeEntityMapper.toEntity(employeeDTO)).thenReturn(employee);
        when(_employeeRepository.save(employee)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class, () -> _employeeService.addEmployees(employeeDTO));
        // Assert
        verifyNoInteractions(_departmentRepository);
        verify(_employeeEntityMapper, times(1)).toEntity(employeeDTO);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void addEmployeeWithDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(employeeDTO.getDepartmentName())).thenReturn(Optional.of(CascadeObj));
        when(_employeeEntityMapper.toEntity(employeeDTO)).thenReturn(employee);
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.addEmployees(employeeDTO));
        // Assert
        verify(_departmentRepository,times(1)).findByName(employeeDTO.getDepartmentName());
        verify(_employeeEntityMapper, times(1)).toEntity(employeeDTO);
        verify(_employeeRepository, times(1)).save(employee);
        Assertions.assertEquals(employeeDTO.getName(),employee.getName());
        Assertions.assertEquals(employeeDTO.getDesignation(),employee.getDesignation());
        Assertions.assertEquals(employeeDTO.getEmail(),employee.getEmail());
        //Assertions.assertEquals(employeeDTO.getPassword(),employee.getPassword());
        Assertions.assertEquals(employeeDTO.getDepartmentName() ,employee.getDepartment().getName());
    }

    @Test
    void addEmployee_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(employeeDTO.getDepartmentName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class, () -> _employeeService.addEmployees(employeeDTO));
        // Assert
        verify(_departmentRepository,times(1)).findByName(employeeDTO.getDepartmentName());
    }

    @Test
    void getAllEmployees_Successfully(){
        // Set Up
        when(_employeeRepository.findAll()).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList = Assertions.assertDoesNotThrow(() -> _employeeService.getAllEmployees());
        // Asset
        verify(_employeeRepository, times(1)).findAll();
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }

    @Test
    void getAllEmployee_ThrowsNoRecordFoundException(){
        // Set Up
        when(_employeeRepository.findAll()).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _employeeService.getAllEmployees());
        // Assert
        verify(_employeeRepository, times(1)).findAll();
        verifyNoInteractions(_employeeEntityMapper);
    }

    @Test
    void getEmployeeById_Successfully(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_employeeEntityMapper.toDTO(employee)).thenReturn(employeeDTO);
        // Act
        EmployeeDTO dbEmployee = Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeeById(id));
        // Asset
        verify(_employeeRepository,times(1)).findById(id);
        verify(_employeeEntityMapper,times(1)).toDTO(employee);
        Assertions.assertEquals(employeeDTO.getName(), dbEmployee.getName());
        Assertions.assertEquals(employeeDTO.getDesignation(), dbEmployee.getDesignation());
        Assertions.assertEquals(employeeDTO.getEmail(), dbEmployee.getEmail());
        //Assertions.assertEquals(employeeDTO.getPassword(), dbEmployee.getPassword());
        Assertions.assertEquals(employeeDTO.getDepartmentName(), dbEmployee.getDepartmentName());
    }

    @Test
    void getEmployeeById_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> _employeeService.getEmployeeById(id));
        // Assert
        verify(_employeeRepository,times(1)).findById(id);
    }

    @Test
    void getEmployeeByEmail_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_employeeEntityMapper.toDTO(employee)).thenReturn(employeeDTO);
        // Act
        EmployeeDTO dbEmployee = Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeeByEmail(email));
        // Asset
        verify(_employeeRepository,times(1)).findByEmail(email);
        verify(_employeeEntityMapper,times(1)).toDTO(employee);
        Assertions.assertEquals(employeeDTO.getName(), dbEmployee.getName());
        Assertions.assertEquals(employeeDTO.getDesignation(), dbEmployee.getDesignation());
        Assertions.assertEquals(employeeDTO.getEmail(), dbEmployee.getEmail());
        //Assertions.assertEquals(employeeDTO.getPassword(), dbEmployee.getPassword());
        Assertions.assertEquals(employeeDTO.getDepartmentName(), dbEmployee.getDepartmentName());
    }

    @Test
    void getEmployeeByEmail_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> _employeeService.getEmployeeByEmail(email));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(email);
    }

    @Test
    void getEmployeeByName_Successfully(){
        // Set Up
        when(_employeeRepository.findAllByName(name)).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList = Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeeByName(name));
        // Asset
        verify(_employeeRepository, times(1)).findAllByName(name);
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }

    @Test
    void getEmployeeByName_ThrowsNoRecordFoundException(){
        // Set Up
        when(_employeeRepository.findAllByName(name)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _employeeService.getEmployeeByName(name));
        // Assert
        verify(_employeeRepository, times(1)).findAllByName(name);
        verifyNoInteractions(_employeeEntityMapper);
    }

    @Test
    void getEmployeeByDesignation_Successfully(){
        // Set Up
        when(_employeeRepository.findAllByDesignation(designation)).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList = Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeesByDesignation(designation));
        // Asset
        verify(_employeeRepository, times(1)).findAllByDesignation(designation);
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }

    @Test
    void getEmployeeByDesignation_ThrowsNoRecordFoundException(){
        // Set Up
        when(_employeeRepository.findAllByDesignation(designation)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _employeeService.getEmployeesByDesignation(designation));
        // Assert
        verify(_employeeRepository, times(1)).findAllByDesignation(designation);
        verifyNoInteractions(_employeeEntityMapper);
    }

    @Test
    void getEmployeeByDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(Cascade)).thenReturn(Optional.of(CascadeObj));
        when(_employeeRepository.findAllByDepartment(CascadeObj)).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList = Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeesByDepartment(Cascade));
        // Asset
        verify(_departmentRepository, times(1)).findByName(Cascade);
        verify(_employeeRepository, times(1)).findAllByDepartment(CascadeObj);
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }

    @Test
    void getEmployeeByDepartment_ThrowsDepartmentNotFoundException(){
        // Set Up
        String department = "Cascade";
        when(_departmentRepository.findByName(department)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class, () -> _employeeService.getEmployeesByDepartment(department));
        // Assert
        verify(_departmentRepository, times(1)).findByName(department);
        verifyNoInteractions(_employeeRepository);
        verifyNoInteractions(_employeeEntityMapper);
    }

    @Test
    void getEmployeeByDepartment_ThrowsNoRecordFoundException(){
        // Set Up
        String department = "Cascade";
        when(_departmentRepository.findByName(department)).thenReturn(Optional.of(CascadeObj));
        when(_employeeRepository.findAllByDepartment(CascadeObj)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class, () -> _employeeService.getEmployeesByDepartment(department));
        // Assert
        verify(_departmentRepository, times(1)).findByName(department);
        verify(_employeeRepository, times(1)).findAllByDepartment(CascadeObj);
        verifyNoInteractions(_employeeEntityMapper);
    }

    @Test
    void getEmployeesByProject_Successfully(){
        // Set Up
        when(_projectRepository.findByName(projectName)).thenReturn(Optional.of(ProjectObj));
        when(_employeeRepository.findAllByProject(ProjectObj)).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList = Assertions.assertDoesNotThrow(()-> _employeeService.getEmployeesByProject(projectName));
        // Asset
        verify(_projectRepository, times(1)).findByName(projectName);
        verify(_employeeRepository, times(1)).findAllByProject(ProjectObj);
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }

    @Test
    void getEmployeesByProject_ThrowsNoRecordFoundException(){
        // Set Up
        when(_projectRepository.findByName(projectName)).thenReturn(Optional.of(ProjectObj));
        when(_employeeRepository.findAllByProject(ProjectObj)).thenReturn(new ArrayList<>());
        // Act
        Assertions.assertThrows(NoRecordFoundException.class,()-> _employeeService.getEmployeesByProject(projectName));
        // Asset
        verify(_projectRepository, times(1)).findByName(projectName);
        verify(_employeeRepository, times(1)).findAllByProject(ProjectObj);
    }

    @Test
    void getEmployeesByProject_ThrowsProjectNotFoundException(){
        // Set Up
        when(_projectRepository.findByName(projectName)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(ProjectNotFoundException.class,()-> _employeeService.getEmployeesByProject(projectName));
        // Asset
        verify(_projectRepository, times(1)).findByName(projectName);
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void deleteEmployeeById_Successfully(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_departmentRepository.findByManager(employee)).thenReturn(Optional.empty());
        doNothing().when(_employeeRepository).delete(employee);
        // Act
        Assertions.assertDoesNotThrow(() -> _employeeService.deleteEmployeeById(id));
        // Assert
        verify(_employeeRepository, times(1)).findById(id);
        verify(_departmentRepository, times(1)).findByManager(employee);
        verify(_employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployeeById_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> _employeeService.deleteEmployeeById(id));
        // Assert
        verify(_employeeRepository,times(1)).findById(id);
    }

    @Test
    void deleteEmployeeById_ForeignKeyException() {
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(manager_cascade));
        when(_departmentRepository.findByManager(any(Employee.class))).thenReturn(Optional.of(CascadeObj));
        // Act & Assert
        Assertions.assertThrows(ForeignKeyException.class, () -> _employeeService.deleteEmployeeById(id));
        // Verify
        verify(_employeeRepository, times(1)).findById(id);
        verify(_departmentRepository, times(1)).findByManager(manager_cascade);
    }

    @Test
    void deleteEmployeeByEmail_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_departmentRepository.findByManager(employee)).thenReturn(Optional.empty());
        doNothing().when(_employeeRepository).delete(employee);
        // Act
        Assertions.assertDoesNotThrow(() -> _employeeService.deleteEmployeeByEmail(email));
        // Assert
        verify(_employeeRepository, times(1)).findByEmail(email);
        verify(_departmentRepository, times(1)).findByManager(employee);
        verify(_employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployeeByEmail_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> _employeeService.deleteEmployeeByEmail(email));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(email);
    }

    @Test
    void deleteEmployeeByEmail_ForeignKeyException() {
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(manager_cascade));
        when(_departmentRepository.findByManager(any(Employee.class))).thenReturn(Optional.of(CascadeObj));
        // Act & Assert
        Assertions.assertThrows(ForeignKeyException.class, () -> _employeeService.deleteEmployeeByEmail(email));
        // Verify
        verify(_employeeRepository, times(1)).findByEmail(email);
        verify(_departmentRepository, times(1)).findByManager(manager_cascade);
    }

    @Test
    void updateEmployeeAllDetailsWithDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.updateEmployeeAllDetails(id, updatedDetails));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findById(id);
        verify(_employeeRepository, times(1)).save(employee);
        Assertions.assertEquals(updatedDetails.getName(),employee.getName());
        Assertions.assertEquals(updatedDetails.getDesignation(),employee.getDesignation());
        Assertions.assertEquals(updatedDetails.getEmail(),employee.getEmail());
        Assertions.assertEquals(updatedDetails.getDepartmentName(),employee.getDepartment().getName());
    }

    @Test
    void updateEmployeeAllDetails_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _employeeService.updateEmployeeAllDetails(id, updatedDetails));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void updateEmployeeAllDetails_ThrowsDepartmentNotFoundException2(){
        // Set Up
        updatedDetails.setDepartmentName(null);
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _employeeService.updateEmployeeAllDetails(id, updatedDetails));
        // Assert
        verifyNoInteractions(_departmentRepository);
        verifyNoInteractions(_employeeRepository);
    }

    @Test
    void updateEmployeeAllDetails_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _employeeService.updateEmployeeAllDetails(id, updatedDetails));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findById(id);
    }

    @Test
    void updateEmployeeAllDetails_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _employeeService.updateEmployeeAllDetails(id, updatedDetails));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findById(id);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeEmail_Successfully(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.updateEmployeeEmail(id, updatedDetails.getEmail()));
        // Assert
        verify(_employeeRepository,times(1)).findById(id);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeEmail_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _employeeService.updateEmployeeEmail(id, updatedDetails.getEmail()));
        // Assert
        verify(_employeeRepository,times(1)).findById(id);
    }

    @Test
    void updateEmployeeEmail_ThrowsRunTimeException(){
        // Set Up
        when(_employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _employeeService.updateEmployeeEmail(id, updatedDetails.getEmail()));
        // Assert
        verify(_employeeRepository,times(1)).findById(id);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeData_Successfully(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.updateEmployeeData(email, change));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(email);
        verify(_employeeRepository, times(1)).save(employee);

        for (Map.Entry<String, String> entry : change.entrySet()) {
            switch (entry.getKey()){
                case "name":
                    Assertions.assertEquals(entry.getValue(), employee.getName());
                    break;
                case "designation":
                    Assertions.assertEquals(entry.getValue(), employee.getDesignation());
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    void updateEmployeeData_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _employeeService.updateEmployeeData(email, change));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(email);
    }

    @Test
    void updateEmployeeData_ThrowsRunTimeException(){
        // Set Up
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _employeeService.updateEmployeeData(email, change));
        // Assert
        verify(_employeeRepository,times(1)).findByEmail(email);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeDepartment_Successfully(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenReturn(null);
        // Act
        assertDoesNotThrow(() -> _employeeService.updateEmployeeDepartment(email, updatedDetails.getDepartmentName()));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findByEmail(email);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployeeDepartment_ThrowsEmployeeNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(EmployeeNotFoundException.class,() -> _employeeService.updateEmployeeDepartment(email, updatedDetails.getDepartmentName()));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findByEmail(email);
    }

    @Test
    void updateEmployeeDepartment_ThrowsDepartmentNotFoundException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.empty());
        // Act
        Assertions.assertThrows(DepartmentNotFoundException.class,() -> _employeeService.updateEmployeeDepartment(email, updatedDetails.getDepartmentName()));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verifyNoInteractions(_employeeRepository);

    }

    @Test
    void updateEmployeeDepartment_ThrowsRunTimeException(){
        // Set Up
        when(_departmentRepository.findByName(updatedDetails.getDepartmentName())).thenReturn(Optional.of(ApolloObj));
        when(_employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(_employeeRepository.save(employee)).thenThrow(new RuntimeException());
        // Act
        Assertions.assertThrows(RuntimeException.class,() -> _employeeService.updateEmployeeDepartment(email, updatedDetails.getDepartmentName()));
        // Assert
        verify(_departmentRepository,times(1)).findByName(updatedDetails.getDepartmentName());
        verify(_employeeRepository,times(1)).findByEmail(email);
        verify(_employeeRepository, times(1)).save(employee);
    }

    @Test
    void getEmployeesWithoutProjects_Successfully(){
        // Set Up
        when(_employeeRepository.findAllWithoutProjects()).thenReturn(employeeList);
        when(_employeeEntityMapper.toDTOList(employeeList)).thenReturn(employeeDTOS);
        // Act
        List<EmployeeDTO> employeeDTOList =  Assertions.assertDoesNotThrow(() -> _employeeService.getEmployeesWithoutProjects());
        // Assert
        verify(_employeeRepository,times(1)).findAllWithoutProjects();
        verify(_employeeEntityMapper, times(1)).toDTOList(employeeList);
        Assertions.assertEquals(employeeDTOS.size(),employeeDTOList.size());
    }
}
