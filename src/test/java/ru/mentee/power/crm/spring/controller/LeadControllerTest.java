package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private LeadService leadService;

  @Autowired private LeadRepository leadRepository;

  @Test
  void leadsEndpointReturns200AndContainsEmail() throws Exception {
    mockMvc
        .perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Email")));
  }

  @Test
  @Disabled
  void leadsGetIdEditReturns200AndContainsLead() throws Exception {
    String id = leadService.findAll().getFirst().getId().toString();
    mockMvc
        .perform(get("/leads/" + id + "/edit"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("lead"))
        .andExpect(view().name("leads/edit"));
  }

  @Test
  @Disabled
  void leadsPostUpdateRedirectsToLeadsAndSaveWithLead() throws Exception {
    String id = leadService.findAll().getFirst().getId().toString();

    mockMvc
        .perform(
            post("/leads/" + id)
                .param("id", id) // hidden field из формы
                .param("email", "new@mail.com")
                .param("company", "New Company")
                .param("status", "QUALIFIED"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    Lead lead = leadRepository.findById(UUID.fromString(id)).get();
    assertThat(lead.getEmail()).isEqualTo("new@mail.com");
    assertThat(lead.getCompany()).isEqualTo("New Company");
    assertThat(lead.getStatus()).isEqualTo("QUALIFIED");
  }

  @Test
  void GetNonExistingLeadReturns404() throws Exception {
    mockMvc.perform(get("/leads/test/edit")).andExpect(status().is4xxClientError());
  }
}
