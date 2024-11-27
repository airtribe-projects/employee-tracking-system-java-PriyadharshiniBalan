package com.airtribe.EmployeeManagementSystem.Entity;

import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long employeeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    @ManyToMany(mappedBy = "employeeSet")
    private Set<Project> project;

    @OneToOne(mappedBy = "manager")
    private Department managedDepartment;

    @OneToOne(mappedBy = "lead")
    private Project managedProject;

    public Employee(String name, String designation, String email) {
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.project = new HashSet<>();
    }
}
