package ru.mentee.power.crm.gate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.gate.domain.Employee;
import ru.mentee.power.crm.gate.dto.*;
import ru.mentee.power.crm.gate.exception.EmployeeNotFoundException;
import ru.mentee.power.crm.gate.mapper.EmployeeMapper;
import ru.mentee.power.crm.gate.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Employee employee = employeeMapper.toEmployee(request);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toCreateEmployeeResponse(savedEmployee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(employeeMapper::toEmployeeDTO).collect(Collectors.toList());
    }

    @Transactional
    public UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException(id));

        employee.setName(request.name());
        employee.setSalary(request.salary());

        return employeeMapper.toUpdateEmployeeResponse(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }
}
