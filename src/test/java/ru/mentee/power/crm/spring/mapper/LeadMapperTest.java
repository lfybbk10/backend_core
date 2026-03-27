package ru.mentee.power.crm.spring.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;

@SpringBootTest
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    CreateLeadRequest request = new CreateLeadRequest("test@mail.com", "NEW");
    Lead lead = leadMapper.toEntity(request);

    assertThat(lead).isNotNull();
    assertThat(lead.getId()).isNull();
    assertThat(lead.getEmail()).isEqualTo("test@mail.com");
    assertThat(lead.getStatus()).isEqualTo("NEW");
  }

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    Lead lead = new Lead();
    UUID id = UUID.randomUUID();
    lead.setId(id);
    lead.setEmail("test@mail.com");
    lead.setStatus("NEW");

    LeadResponse leadResponse = leadMapper.toResponse(lead);
    assertThat(leadResponse).isNotNull();
    assertThat(leadResponse.getId()).isEqualTo(id);
    assertThat(leadResponse.getStatus()).isEqualTo("NEW");
    assertThat(leadResponse.getEmail()).isEqualTo("test@mail.com");
  }
}
