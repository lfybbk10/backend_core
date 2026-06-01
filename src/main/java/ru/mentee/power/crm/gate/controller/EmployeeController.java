package ru.mentee.power.crm.gate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.gate.domain.Employee;
import ru.mentee.power.crm.gate.dto.*;
import ru.mentee.power.crm.gate.service.EmployeeService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        CreateEmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.created(URI.create("/employees")).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employeeDTOS = employeeService.getAllEmployees();
        return ResponseEntity.ok(employeeDTOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateEmployeeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        UpdateEmployeeResponse response = employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}
