package com.mustafa.restourant.repository;

import com.mustafa.restourant.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeesRepository extends JpaRepository<Employees, Integer> {
}
