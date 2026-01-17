package ru.mentee.power.crm;

import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.http.*;
import java.net.URI;
import static org.assertj.core.api.Assertions.*;

/**
 * Интеграционный тест сравнения Servlet и Spring Boot стеков.
 * Запускает оба сервера, выполняет HTTP запросы, сравнивает результаты.
 */
class StackComparisonTest {

    private static final int SERVLET_PORT = 8080;
    private static final int SPRING_PORT = 8081;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
    void shouldReturnLeadsFromBothStacks() throws Exception {
        // Given: HTTP запросы к обоим стекам
        HttpRequest servletRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
                .GET()
                .build();

        HttpRequest springRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
                .GET()
                .build();

        // When: выполняем запросы
        HttpResponse<String> servletResponse = httpClient.send(
                servletRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> springResponse = httpClient.send(
                springRequest, HttpResponse.BodyHandlers.ofString());

        // Then: оба возвращают 200 OK и содержат таблицу
        assertThat(servletResponse.statusCode()).isEqualTo(200);
        assertThat(springResponse.statusCode()).isEqualTo(200);

        assertThat(servletResponse.body()).contains("<table");
        assertThat(springResponse.body()).contains("<table");

        int servletRows = countTableRows(servletResponse.body());
        int springRows = countTableRows(springResponse.body());

        assertThat(servletRows)
                .as("Количество лидов должно совпадать")
                .isEqualTo(springRows);

        System.out.printf("Servlet: %d лидов, Spring: %d лидов%n",
                servletRows, springRows);
    }

    /**
     * Подсчитывает количество строк <tr> в HTML (количество лидов в таблице).
     */
    private int countTableRows(String html) {
        return html.split("<tr ").length - 1;
    }

    @Test
    @DisplayName("Измерение времени старта обоих стеков")
    void shouldMeasureStartupTime() throws Exception {
        // Servlet startup time (уже запущен вручную)
        long servletStartupMs = measureServletStartup();

        // Spring Boot startup time (уже запущен вручную)
        long springStartupMs = measureSpringBootStartup();

        // Вывод результатов
        System.out.println("=== Сравнение времени старта ===");
        System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
        System.out.printf("Spring Boot: %d ms%n", springStartupMs);
        System.out.printf("Разница: Spring %s на %d ms%n",
                springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
                Math.abs(springStartupMs - servletStartupMs));

        // Просто фиксируем что оба стартуют за разумное время
        assertThat(servletStartupMs).isLessThan(10_000);
        assertThat(springStartupMs).isLessThan(15_000);
    }

    private long measureServletStartup() throws Exception {
        long startTime = System.nanoTime();
        ru.mentee.power.crm.Main.main(new String[]{});
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000 ;
    }

    private long measureSpringBootStartup() {
        long startTime = System.nanoTime();
        SpringApplication.run(ru.mentee.power.crm.spring.Application.class,
                new String[]{});
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000 ;
    }
}