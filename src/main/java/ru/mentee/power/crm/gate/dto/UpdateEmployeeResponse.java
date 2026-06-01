package ru.mentee.power.crm.gate.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateEmployeeResponse(UUID id, String name, BigDecimal salary) {
}
