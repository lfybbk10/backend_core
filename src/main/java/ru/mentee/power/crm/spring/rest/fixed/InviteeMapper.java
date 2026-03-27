package ru.mentee.power.crm.spring.rest.fixed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper // componentModel и unmappedTargetPolicy заданы глобально в compilerArgs
public interface InviteeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Invitee toEntity(CreateInviteeRequest dto);

  InviteeResponse toResponse(Invitee entity);
}
