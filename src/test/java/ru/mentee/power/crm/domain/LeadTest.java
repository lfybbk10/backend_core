package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {
    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String id = lead.getId();

        // Then
        assertThat(id).isEqualTo("L1");
    }

    // Допиши тесты для email, phone, company, status, toString
    @Test
    void shouldReturnEmail_whenGetEmailCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String email = lead.getEmail();

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String phone = lead.getPhone();

        // Then
        assertThat(phone).isEqualTo("+71234567890");
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String company = lead.getCompany();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String status = lead.getStatus();

        // Then
        assertThat(status).isEqualTo("NEW");
    }

    @Test
    void shouldReturnToString_whenGetToStringCalled() {
        // Given
        Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

        // When
        String toString = lead.toString();

        // Then
        assertThat(toString).isEqualTo("Lead{id='L1', email='test@example.com', phone='+71234567890', company='TestCorp', status='NEW'}");
    }
}