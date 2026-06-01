package ru.mentee.power.crm.gate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateEmployeeRequest(@NotNull String name, @NotNull @Min(value = 0) BigDecimal salary) {
}
