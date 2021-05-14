package com.mustafa.restourant.service;

import com.mustafa.restourant.entity.Employees;
import com.mustafa.restourant.exception.NotFoundException;
import com.mustafa.restourant.repository.EmployeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    public Employees findById(int id) throws NotFoundException{
      Optional<Employees> employee = employeesRepository.findById(id);
      if(employee.isPresent()){
          return employee.get();
      }else{
          throw new NotFoundException("Çalışan bulunamadı");
      }
    }

    @Override
    public Page<Employees> allEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return employeesRepository.findAll(pageable);
    }

    @Override
    public List<Employees> getAllEmployes() {
        return employeesRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));
    }
}
