package ru.mentee.power.crm.gate.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(UUID id) {
        super("Employee not found with ID: " + id);
    }
}
