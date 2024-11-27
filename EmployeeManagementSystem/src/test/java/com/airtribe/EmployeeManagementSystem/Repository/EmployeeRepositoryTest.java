package com.airtribe.EmployeeManagementSystem.Repository;

import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository _employeeRepository;

    @Autowired
    private ProjectRepository _projectRepository;

    @Autowired
    private DepartmentRepository _departmentRepository;

    private Employee employee;

    private Employee manager;

    private Employee lead;

    private Set<Employee> empset;

    private List<Employee> employeeList;

    private Department department;

    private Project project;

    private String name;

    private String designation;

    @BeforeEach
    void setUp(){
        name = "Priyadharshini B";
        designation = "Employee";

        employee = _employeeRepository.save(new Employee("Priyadharshini B","Employee","priyanka.a@gmail.com"));
        manager = _employeeRepository.save(new Employee("Srinivasan","Manager","srini.a@gmail.com"));
        lead = _employeeRepository.save(new Employee("Priyadharshini B","Employee","priya.a@gmail.com"));

        department = new Department("Cascade","Manages Assets","DLF 1-A",manager);
        department = _departmentRepository.save(department);

        employeeList = new ArrayList<>();
        employeeList.add(employee);
        employeeList.add(lead);
        employeeList.add(manager);

        project = new Project("SSS", "Seller Self Service", 200000L, lead, department);
        empset = new HashSet<>();
        empset.add(manager);
        empset.add(lead);
    }

    @Test
    public void testSaveEmployee(){
        Employee expEmployee = _employeeRepository.save(employee);
        Assertions.assertEquals(expEmployee.getName(), employee.getName());
        Assertions.assertEquals(expEmployee.getEmail(), employee.getEmail());
        Assertions.assertEquals(expEmployee.getDesignation(), employee.getDesignation());
    }

    @Test
    public void testFindByEmail(){
        Employee expEmployee = _employeeRepository.findByEmail(manager.getEmail()).get();
        Assertions.assertEquals(expEmployee.getName(), manager.getName());
        Assertions.assertEquals(expEmployee.getEmail(), manager.getEmail());
        Assertions.assertEquals(expEmployee.getDesignation(), manager.getDesignation());
    }

    @Test
    public void testFindAll(){
        _employeeRepository.saveAll(employeeList);
        List<Employee> employees = _employeeRepository.findAll();
        Assertions.assertEquals(employees.size(), employeeList.size());
    }

    @Test
    public void testFindAllByName(){
        _employeeRepository.saveAll(employeeList);
        List<Employee> employees = _employeeRepository.findAllByName(name);
        employeeList.remove(2);
        Assertions.assertEquals(employees.size(),employeeList.size());
    }

    @Test
    public void testFindAllByDesignation(){
        _employeeRepository.saveAll(employeeList);
        List<Employee> employees = _employeeRepository.findAllByDesignation(designation);
        employeeList.remove(2);
        Assertions.assertEquals(employees.size(),employeeList.size());
    }

    @Test
    public void testFindAllByProject(){
        project.setEmployeeSet(empset);
        project = _projectRepository.save(project);
        Project project = _projectRepository.findById(1L).get();
        List<Employee> employees = _employeeRepository.findAllByProject(project);
        Assertions.assertEquals(empset.size(),employees.size());
    }

    @Test
    public void testFindAllWithoutProjects(){
        project.setEmployeeSet(empset);
        project = _projectRepository.save(project);
        List<Employee> employees = _employeeRepository.findAllWithoutProjects();
        Assertions.assertEquals(employeeList.size()-empset.size(),employees.size());
    }

    @Test
    public void testDelete(){
        _employeeRepository.delete(employee);
        List<Employee> employees = _employeeRepository.findAll();
        Assertions.assertEquals(employeeList.size()-1, employees.size());
    }
}
