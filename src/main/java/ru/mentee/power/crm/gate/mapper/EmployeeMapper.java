package ru.mentee.power.crm.gate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.power.crm.gate.domain.Employee;
import ru.mentee.power.crm.gate.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.gate.dto.CreateEmployeeResponse;
import ru.mentee.power.crm.gate.dto.EmployeeDTO;
import ru.mentee.power.crm.gate.dto.UpdateEmployeeResponse;

@Mapper
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    Employee toEmployee(CreateEmployeeRequest request);

    CreateEmployeeResponse toCreateEmployeeResponse(Employee employee);

    UpdateEmployeeResponse toUpdateEmployeeResponse(Employee employee);

    EmployeeDTO toEmployeeDTO(Employee employee);
}
