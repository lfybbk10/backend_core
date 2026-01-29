package ru.mentee.power.crm.spring.controller;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.MockLeadService;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void shouldReturnLeadsWithTest1WithQuery() throws Exception {
        String search = "test1";
        when(leadService.findLeads(search, "")).
                thenReturn(Arrays.asList(new Lead(UUID.randomUUID(), "test1@mail.ru", "c", "NEW")));

        mockMvc.perform(get("/leads?search=test1"))
                .andExpect(model().attribute("leads", everyItem(
                        hasProperty("email", containsStringIgnoringCase("test1")))));
    }

    @Test
    void ShouldReturnAllLeads() throws Exception {

        Lead testLead1 = new Lead(UUID.randomUUID(), "test1", "c", "NEW");
        Lead testLead2 = new Lead(UUID.randomUUID(), "test2", "c", "NEW");
        when(leadService.findLeads(null, null))
            .thenReturn(Arrays.asList(testLead1, testLead2));

        mockMvc.perform(get("/leads"))
                .andExpect(model().attribute("leads", containsInAnyOrder(testLead1, testLead2)));
    }

    @Test
    void shouldReturnLeadsWithTest2WithCombinedQuery() throws Exception {
        String search = "test1";
        String status = "NEW";

        when(leadService.findLeads(search, status))
                .thenReturn(List.of(new Lead(UUID.randomUUID(), "test1@mail.ru", "c", "NEW")));

        MvcResult res = mockMvc.perform(get("/leads")
                        .param("search", search)
                        .param("status", status))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<Lead> leads = (List<Lead>) res.getModelAndView().getModel().get("leads");

        assertThat(leads).isNotEmpty();
        assertThat(leads).allMatch(l -> l.email().toLowerCase().contains("test1"));
        assertThat(leads).allMatch(l -> l.status().contains("NEW"));
    }
}