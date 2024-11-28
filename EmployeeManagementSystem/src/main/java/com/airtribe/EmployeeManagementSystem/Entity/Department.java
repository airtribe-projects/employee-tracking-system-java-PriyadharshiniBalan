package com.airtribe.EmployeeManagementSystem.Entity;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long departmentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToOne
    @JoinColumn(name = "managerId", nullable = false)
    private Employee manager;

    private String location;

    @OneToMany(mappedBy = "department")
    private List<Employee> employeeList;

    @OneToMany(mappedBy = "department")
    private List<Project> project;

    public Department(String name, String description, String location, Employee manager) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.manager = manager;
        this.project = new ArrayList<>();
        this.employeeList = new ArrayList<>();
    }
}
