// EmailValidationClient.java
package ru.mentee.power.crm.spring.client;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EmailValidationClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailValidationClient(
      RestTemplate restTemplate, @Value("${email.validation.base-url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public EmailValidationResponse validateEmail(String email) {
    System.out.println("Client baseUrl = " + baseUrl);
    String url =
        UriComponentsBuilder.fromUri(URI.create(baseUrl))
            .path("/api/validate/email")
            .queryParam("email", email)
            .toUriString();

    try {
      EmailValidationResponse response =
          restTemplate.getForObject(url, EmailValidationResponse.class);

      if (response == null) {
        throw new RuntimeException("Email validation service returned null response");
      }

      return response;
    } catch (RestClientException e) {
      throw new RuntimeException("Failed to validate email: " + email + " " + e, e);
    }
  }
}
