package ru.mentee.power.crm.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadListServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private ServletContext servletContext;
    @Mock private ServletConfig servletConfig;

    @Mock private LeadService leadService;

    private LeadListServlet servlet;

    @BeforeEach
    void setUp() throws ServletException {
        servlet = new LeadListServlet();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        servlet.init(servletConfig);
        when(servletContext.getAttribute("leadService")).thenReturn(leadService);
    }

    @Test
    @Disabled
    void shouldReturnHtmlTable_whenDoGetCalled() throws ServletException, IOException {
        Lead lead1 = new Lead(UUID.randomUUID(), "test1@mail.com", new Company("Comp"), "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), "test2@mail.com", new Company("Comp1"), "NEW");

        when(leadService.findAll()).thenReturn(Arrays.asList(lead1, lead2));

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        servlet.doGet(request, response);

        verify(leadService, times(1)).findAll();

        String html = body.toString();
        assertTrue(html.contains("<table"), "Ответ должен содержать <table");
        assertTrue(html.contains("</table>"), "Ответ должен содержать </table>");

        // Проверяем, что данные реально попали в HTML
        assertTrue(html.contains("test1@mail.com"), "Ответ должен содержать email первого лида");
        assertTrue(html.contains("test2@mail.com"), "Ответ должен содержать email второго лида");
    }

    @Test
    @Disabled
    void shouldSetContentTypeToHtml_whenDoGetCalled() throws IOException, ServletException {
        when(leadService.findAll()).thenReturn(List.of());
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        servlet.doGet(request, response);
        verify(response, times(1)).setContentType("text/html; charset=UTF-8");
    }
}
