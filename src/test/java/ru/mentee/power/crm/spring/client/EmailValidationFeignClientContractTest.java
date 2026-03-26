package ru.mentee.power.crm.spring.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@WireMockTest
class EmailValidationFeignClientContractTest {

  @Autowired private EmailValidationFeignClient feignClient;

  @RegisterExtension
  static WireMockExtension wm =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("email.validation.base-url", wm::baseUrl);
  }

  @Test
  void shouldReturnValidResponse_whenEmailIsValid() {
    // Given: Contract - external API returns valid=true for valid email
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("john@example.com"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "john@example.com",
                    "valid": true,
                    "reason": "Email exists and is deliverable"
                }
                """)));

    // When: Feign client makes HTTP call
    EmailValidationResponse response = feignClient.validateEmail("john@example.com");

    // Then: Response correctly deserialized
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("john@example.com");

    // Verify: HTTP call was made correctly
    wm.verify(
        getRequestedFor(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("john@example.com")));
  }

  @Test
  void shouldReturnInvalidResponse_whenEmailIsInvalid() {
    // Given: Contract - external API returns valid=false
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("invalid@bad.email"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "invalid@bad.email",
                    "valid": false,
                    "reason": "Domain does not accept email"
                }
                """)));

    // When
    EmailValidationResponse response = feignClient.validateEmail("invalid@bad.email");

    // Then
    assertThat(response.valid()).isFalse();
  }

  @Test
  void shouldThrowFeignException_whenExternalServiceReturns500() {
    // Given: External API returns 500 Internal Server Error
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Internal Server Error")));

    // When/Then: Feign throws exception
    assertThatThrownBy(() -> feignClient.validateEmail("any@email.com"))
        .isInstanceOf(feign.FeignException.class);
  }

  @Test
  void shouldThrowFeignException_whenExternalServiceReturns400() {
    // Given: External API returns 400 Bad Request
    wm.stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid email format\"}")));

    // When/Then: Feign throws exception for 4xx errors
    assertThatThrownBy(() -> feignClient.validateEmail("not-an-email"))
        .isInstanceOf(feign.FeignException.BadRequest.class);
  }
}
