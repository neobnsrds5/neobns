package com.example.clientOne.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clientOne.model.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Long>{

	void deleteEmployeeById(Long id);

	Optional<Employee> findEmployeeById(Long id);
	
	
	

}
