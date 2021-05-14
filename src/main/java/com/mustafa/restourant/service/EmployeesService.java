package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Employees;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeesService {
    void saveEmployee(Employees employee);
    void deleteEmployeeById(int id);
    Employees findById(int id);
    Page<Employees> allEmployees(int page, int size);
    List<Employees> getAllEmployes();
}
