package com.airtribe.EmployeeManagementSystem.Repository;

import com.airtribe.EmployeeManagementSystem.Entity.Department;
import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    List<Employee> findAllByName(String name);

    List<Employee> findAllByDesignation(String designation);

    List<Employee> findAllByDepartment(Department department);

    List<Employee> findAllByProject(Project project);

    @Query(value = "SELECT e.* FROM employee_projects ep RIGHT JOIN employee e ON ep.employee_id = e.employee_id WHERE ep.employee_id is null", nativeQuery = true)
    List<Employee> findAllWithoutProjects();
}
