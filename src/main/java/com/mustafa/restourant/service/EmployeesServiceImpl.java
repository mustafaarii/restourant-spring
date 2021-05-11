package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Employees;
import com.mustafa.restourant.repository.EmployeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class EmployeesServiceImpl implements EmployeesService{
    @Autowired
    EmployeesRepository employeesRepository;

    @Override
    public void saveEmployee(Employees employee) {
        employeesRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(int id) { employeesRepository.deleteById(id); }

    @Override
    public Page<Employees> allEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return employeesRepository.findAll(pageable);
    }
}
