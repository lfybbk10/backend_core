package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class LeadTest {
  @Test
  void shouldReturnId_whenGetIdCalled() {
    // Given
    UUID randomUUID = UUID.randomUUID();
    Address address = new Address("New York", "Times Square", "123");
    Lead lead = new Lead(randomUUID, "John@gmail.com", new Company("TestCorp"), "NEW");

    // When
    UUID id = lead.getId();

    // Then
    assertThat(id).isEqualTo(randomUUID);
  }

  // Допиши тесты для email, phone, company, status, toString
  @Test
  void shouldReturnEmail_whenGetEmailCalled() {
    // Given
    UUID randomUUID = UUID.randomUUID();
    Address address = new Address("New York", "Times Square", "123");
    Lead lead = new Lead(randomUUID, "John@gmail.com", new Company("TestCorp"), "NEW");
    // When
    String email = lead.getEmail();

    // Then
    assertThat(email).isEqualTo("John@gmail.com");
  }

  @Test
  void shouldReturnCompany_whenGetCompanyCalled() {
    // Given
    UUID randomUUID = UUID.randomUUID();
    Address address = new Address("New York", "Times Square", "123");
    Lead lead = new Lead(randomUUID, "John@gmail.com", new Company("TestCorp"), "NEW");
    // When
    Company company = lead.getCompany();

    // Then
    assertThat(company.getName()).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnStatus_whenGetStatusCalled() {
    // Given
    UUID randomUUID = UUID.randomUUID();
    Address address = new Address("New York", "Times Square", "123");
    Lead lead = new Lead(randomUUID, "John@gmail.com", new Company("TestCorp"), "NEW");
    // When
    String status = lead.getStatus();

    // Then
    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldBeEqual_whenSameIdButDifferentContact() {
    // Given
    UUID randomUUID = UUID.randomUUID();
    Address address = new Address("New York", "Times Square", "123");
    Lead lead1 = new Lead(randomUUID, "John@gmail.com", new Company("TestCorp"), "NEW");
    Lead lead2 = new Lead(randomUUID, "Test@gmail.com", new Company("TestCorp"), "NEW");

    assertThat(lead1).isEqualTo(lead2);
  }
}
