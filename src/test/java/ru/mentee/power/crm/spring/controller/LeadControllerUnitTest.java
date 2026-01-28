package ru.mentee.power.crm.spring.controller;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.spring.MockLeadService;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeadController.class)
class LeadControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeadService leadService;

    @Test
    void shouldCreateControllerWithoutSpring() {
        // Given: mock service без Spring контейнера
        MockLeadService mockService = new MockLeadService();

        // When: создаём контроллер через конструктор (pure Java)
        LeadController controller = new LeadController(mockService);

        // Then: контроллер работает, использует mock service
        String response = controller.home();
        assertThat(response).contains("2 leads"); // MockLeadService возвращает 2 лида
    }

    @Test
    void shouldUseInjectedService() {
        // Given
        MockLeadService mockService = new MockLeadService();
        LeadController controller = new LeadController(mockService);

        // When: вызываем метод контроллера
        String response = controller.home();

        // Then: сервис использован (не null)
        assertThat(response).isNotNull();
        assertThat(response).contains("Spring Boot CRM is running");
    }

    @Test
    void shouldDeleteLeadAndRedirect() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/leads/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/leads"));

        verify(leadService).delete(id);
    }

    @Test
    void deleteNonexistentId_shouldReturn404_andExposeResponseStatusException() throws Exception {
        UUID nonexistent = UUID.randomUUID();

        // Сервис кидает 404
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"))
                .when(leadService).delete(nonexistent);

        mockMvc.perform(post("/leads/{id}/delete", nonexistent))
                .andExpect(status().isNotFound())
                // Проверяем, что именно это исключение "всплыло" до MVC-слоя
                .andExpect(result ->
                        org.assertj.core.api.Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                );

        verify(leadService).delete(nonexistent);
    }
}