package ru.mentee.power.crm.spring.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.power.crm.domain.Lead;

@SpringBootTest
@WireMockTest
class LeadServiceRetryTest {

  @Autowired private LeadService leadService;

  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("email.validation.base-url", wm::baseUrl);
    // Ускоряем retry для тестов
    registry.add("resilience4j.retry.instances.email-validation.wait-duration", () -> "100ms");
  }

  @Test
  void shouldRetryAndSucceed_whenFirstAttemptFails() {
    // Given: Первые 2 вызова возвращают 500, третий — успех
    // WireMock Scenarios позволяют менять ответ между вызовами
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(serverError())
            .willSetStateTo("First Retry"));

    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs("First Retry")
            .willReturn(serverError())
            .willSetStateTo("Second Retry"));

    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs("Second Retry")
            .willReturn(
                okJson(
                    """
                {"email": "testt@example.com", "valid": true, "reason": "OK"}
                """)));

    // When: создаём лида
    Lead lead = new Lead();
    lead.setEmail("testt@example.com");
    lead.setStatus("NEW");
    Lead created = leadService.createLead(lead);

    // Then: лид создан после 3 попыток
    assertThat(created).isNotNull();

    // Verify: было ровно 3 HTTP вызова
    wm.verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldUseFallback_whenAllRetriesFail() {
    // Given: Все 3 вызова возвращают 500
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Service Unavailable")));

    // When: создаём лида
    Lead lead = new Lead();
    lead.setEmail("test@example.com");
    lead.setStatus("NEW");
    Lead created = leadService.createLead(lead);

    // Then: fallback сработал — лид создан без валидации
    assertThat(created).isNotNull();

    // Verify: было 3 попытки (max-attempts)
    wm.verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldNotRetry_whenClientErrorOccurs() {
    // Given: 400 Bad Request (клиентская ошибка)
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));

    // When/Then: исключение без retry
    // 4xx ошибки в ignore-exceptions — не повторяем
    Lead lead = new Lead();
    lead.setEmail("invalid");
    lead.setStatus("NEW");

    // В зависимости от реализации: либо исключение, либо fallback
    // Проверяем что был только 1 вызов (без retry)
    try {
      leadService.createLead(lead);
    } catch (Exception ignored) {
      // Ожидаем исключение для 4xx
    }

    // Verify: только 1 попытка — retry НЕ сработал для 4xx
    wm.verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldRetry_whenTimeoutOccurs() {
    // Given: Первый вызов — timeout, второй — успех
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(ok().withFixedDelay(10000)) // 10 секунд — больше timeout
            .willSetStateTo("After Timeout"));

    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs("After Timeout")
            .willReturn(
                okJson(
                    """
                {"email": "ttest@example.com", "valid": true, "reason": "OK"}
                """)));

    // When: создаём лида (первый вызов timeout, второй успех)
    Lead lead = new Lead();
    lead.setEmail("ttest@example.com");
    lead.setStatus("NEW");
    Lead created = leadService.createLead(lead);

    // Then: лид создан после retry
    assertThat(created).isNotNull();

    // Verify: было 2 попытки
    wm.verify(2, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }
}
