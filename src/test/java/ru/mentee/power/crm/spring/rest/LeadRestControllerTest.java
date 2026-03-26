package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

@WebMvcTest(LeadRestController.class)
class LeadRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService leadService;

  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    when(leadService.findAll()).thenReturn(List.of());
    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  @Disabled
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    UUID id = UUID.randomUUID();

    Lead lead = new Lead();
    lead.setEmail("test@mail.ru");
    lead.setStatus("NEW");

    Lead createdLead = new Lead();
    createdLead.setEmail("test@mail.ru");
    createdLead.setId(id);
    createdLead.setStatus("NEW");

    LeadResponse createdLeadResponse = new LeadResponse(id, "test@mail.ru", "NEW", null, null);
    when(leadService.createLead(any(Lead.class))).thenReturn(createdLead);

    when(leadMapper.toResponse(any(Lead.class))).thenReturn(createdLeadResponse);

    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"test@mail.ru\", \"status\": \"NEW\" }"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", "/api/leads/" + id));
  }

  @Test
  void shouldReturn204_whenDeleteExistingLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.delete(id)).thenReturn(true);

    mockMvc
        .perform(delete("/api/leads/{id}", id))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    when(leadService.delete(id)).thenReturn(false);

    mockMvc.perform(delete("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }
}
