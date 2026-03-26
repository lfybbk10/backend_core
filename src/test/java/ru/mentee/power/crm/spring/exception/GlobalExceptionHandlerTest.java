package ru.mentee.power.crm.spring.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.LeadRestController;
import ru.mentee.power.crm.spring.service.LeadService;

@WebMvcTest(LeadRestController.class)
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private LeadService service;

  @MockitoBean private LeadMapper leadMapper;

  @Test
  void shouldReturn404_whenEntityNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    when(service.findById(any(UUID.class)))
        .thenThrow(new EntityNotFoundException("Lead", id.toString()));

    mockMvc
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Lead not found with id: " + id))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }

  @Test
  void shouldReturn400WithFieldErrors_whenValidationFails() throws Exception {
    String invalidJson =
        """
                {
                  "email": "",
                  "firstName": ""
                }
                """;

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.errors").exists())
        .andExpect(jsonPath("$.errors.email").exists());
  }

  @Test
  void shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
    UUID id = UUID.randomUUID();

    when(service.findById(any(UUID.class))).thenThrow(new RuntimeException("some internal error"));

    mockMvc
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("Unexpected error"))
        .andExpect(jsonPath("$.message").value("Internal server error occurred. Contact support."))
        .andExpect(
            jsonPath("$.message")
                .value(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("RuntimeException"))))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }
}
