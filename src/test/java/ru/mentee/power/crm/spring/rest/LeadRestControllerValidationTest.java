package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerValidationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private LeadService leadService; // Mock Service слой

  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn400_whenEmailIsBlank() throws Exception {
    // Given: CreateLeadRequest с пустым email
    CreateLeadRequest request = new CreateLeadRequest("", "NEW");

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400_whenEmailIsInvalidFormat() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("test", "NEW");

    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn201_whenAllFieldsAreValid() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("test@mail.com", "NEW");

    String requestJson = objectMapper.writeValueAsString(request);

    Lead lead = new Lead();
    lead.setEmail("test@mail.com");
    lead.setStatus("NEW");

    when(leadService.createLead(any())).thenReturn(lead);
    when(leadMapper.toResponse(any()))
        .thenReturn(
            new LeadResponse(UUID.randomUUID(), "test@mail.com", "NEW", "comp", Instant.now()));

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated());
  }
}
