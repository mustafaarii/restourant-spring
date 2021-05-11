package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Employees;
import org.springframework.data.domain.Page;

public interface EmployeesService {
    void saveEmployee(Employees employee);
    void deleteEmployeeById(int id);
    Page<Employees> allEmployees(int page, int size);
}
