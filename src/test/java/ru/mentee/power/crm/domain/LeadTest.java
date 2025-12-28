package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {
    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Lead lead = new Lead(randomUUID, "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        UUID id = lead.getId();

        // Then
        assertThat(id).isEqualTo(randomUUID);
    }

    // Допиши тесты для email, phone, company, status, toString
    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String email = lead.getEmail();

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String phone = lead.getPhone();

        // Then
        assertThat(phone).isEqualTo("+71234567890");
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String company = lead.getCompany();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        Lead lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String status = lead.getStatus();

        // Then
        assertThat(status).isEqualTo("NEW");
    }

    @Test
    void shouldReturnToString_whenGetToStringCalled() {
        // Given
        UUID uuid = UUID.randomUUID();
        Lead lead = new Lead(uuid, "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String toString = lead.toString();

        // Then
        assertThat(toString).isEqualTo("Lead{id="+"'"+uuid+"'"+", email='test@example.com', phone='+71234567890', company='TestCorp', status='NEW'}");
    }
}