package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;

@Mapper // componentModel и unmappedTargetPolicy заданы глобально в compilerArgs
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "deals", ignore = true)
  Lead toEntity(CreateLeadRequest dto);

  @Mapping(target = "company", source = "company.name")
  LeadResponse toResponse(Lead entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "deals", ignore = true)
  void updateEntity(UpdateLeadRequest dto, @MappingTarget Lead entity);
}
