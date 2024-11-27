package com.airtribe.EmployeeManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long budget;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    @ManyToMany
    @JoinTable(
            name = "employee_projects",
            joinColumns = @JoinColumn(name = "projectId"),
            inverseJoinColumns = @JoinColumn(name = "employeeId")
    )
    private Set<Employee> employeeSet;

    @OneToOne
    @JoinColumn(name = "leadId", nullable = false)
    private Employee lead;

    public Project(String name, String description, Long budget, Employee lead, Department department) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.lead = lead;
        this.department = department;
        this.employeeSet = new HashSet<>();
    }
}
