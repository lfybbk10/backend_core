package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LeadTest {
    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Lead lead = new Lead(randomUUID, "John@gmail.com", "TestCorp", "NEW");

        // When
        UUID id = lead.id();

        // Then
        assertThat(id).isEqualTo(randomUUID);
    }

    // Допиши тесты для email, phone, company, status, toString
    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Lead lead = new Lead(randomUUID, "John@gmail.com", "TestCorp", "NEW");
        // When
        String email = lead.email();

        // Then
        assertThat(email).isEqualTo("John@gmail.com");
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Lead lead = new Lead(randomUUID, "John@gmail.com", "TestCorp", "NEW");
        // When
        String company = lead.company();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Lead lead = new Lead(randomUUID, "John@gmail.com", "TestCorp", "NEW");
        // When
        String status = lead.status();

        // Then
        assertThat(status).isEqualTo("NEW");
    }

    @Test
    void shouldBeEqual_whenSameIdButDifferentContact() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Lead lead1 = new Lead(randomUUID, "John@gmail.com", "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, "Test@gmail.com", "TestCorp", "NEW");

        assertThat(lead1).isEqualTo(lead2);
    }

    @Test
    void shouldThrowException_whenInvalidStatus() {
        Address address = new Address("New York", "Times Square", "123");
        assertThatIllegalArgumentException().isThrownBy(() -> new Lead(UUID.randomUUID(), "John@gmail.com", "TestCorp", "INVALID"));
    }
}