package com.airtribe.EmployeeManagementSystem.Repository.DataLoader;

import com.airtribe.EmployeeManagementSystem.Entity.Employee;
import com.airtribe.EmployeeManagementSystem.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminDataLoader implements ApplicationRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!employeeRepository.findByEmail(adminEmail).isPresent()) {
            Employee admin = new Employee();
            admin.setName(adminUsername);
            admin.setEmail(adminEmail);
            admin.setDesignation("ADMIN");
            employeeRepository.save(admin);
            System.out.println("Saved admin user: " + adminUsername);
        } else {
            System.out.println("Admin user already exists with email: " + adminEmail);
        }
    }
}