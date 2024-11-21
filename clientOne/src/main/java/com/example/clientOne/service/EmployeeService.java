package com.example.clientOne.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.clientOne.exception.UserNotFoundException;
import com.example.clientOne.model.Employee;
import com.example.clientOne.repo.EmployeeRepo;

@Service
public class EmployeeService {
	
	private final EmployeeRepo employeeRepo;

	@Autowired
	public EmployeeService(EmployeeRepo employeeRepo) {
		super();
		this.employeeRepo = employeeRepo;
	}
	
	public Employee addEmployee(Employee employee) {
		
		employee.setEmployeeCode(UUID.randomUUID().toString());
		return employeeRepo.save(employee);
		
	}
	
	public List<Employee> findAllEmployees(){
		return employeeRepo.findAll();
	}

	public Employee updatEmployee(Employee employee) {
		return employeeRepo.save(employee);
	}
	
	public Employee findEmployeeById(Long id) {
		return employeeRepo.findEmployeeById(id)
				.orElseThrow(()->new UserNotFoundException("User by id " + id + " not found"));
	}
	
	public void deleteEmployee(Long id) {
		employeeRepo.deleteEmployeeById(id);
	}
}
