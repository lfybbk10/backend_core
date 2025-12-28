package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LeadTest {
    @Test
    void shouldCreateLead_whenValidData() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then
        assertThat(lead.contact()).isEqualTo(contact);
    }

    @Test
    void shouldReturnId_whenGetIdCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

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
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        // When
        String email = lead.contact().email();

        // Then
        assertThat(email).isEqualTo("John@gmail.com");
    }

    @Test
    void shouldReturnPhone_whenGetPhoneCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        // When
        String phone = lead.contact().phone();

        // Then
        assertThat(phone).isEqualTo("+79122543221");
    }

    @Test
    void shouldReturnCompany_whenGetCompanyCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        // When
        String company = lead.company();

        // Then
        assertThat(company).isEqualTo("TestCorp");
    }

    @Test
    void shouldAccessEmailThroughDelegation_whenLeadCreated() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        // When
        String city = lead.contact().address().city();

        // Then
        assertThat(city).isEqualTo("New York");
    }

    @Test
    void shouldReturnStatus_whenGetStatusCalled() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
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
        Contact contact1 = new Contact("John@gmail.com", "+79122543221", address);
        Contact contact2 = new Contact("Test@gmail.com", "+79122213221", address);
        Lead lead1 = new Lead(randomUUID, contact1, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact2, "TestCorp", "NEW");

        assertThat(lead1).isEqualTo(lead2);
    }

    @Test
    void shouldThrowException_whenContactIsNull() {
        assertThatNullPointerException().isThrownBy(() -> new Lead(UUID.randomUUID(), null, "TestCorp", "NEW"));
    }

    @Test
    void shouldThrowException_whenInvalidStatus() {
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        assertThatIllegalArgumentException().isThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCorp", "INVALID"));
    }

    @Test
    void shouldDemonstrateThreeLevelComposition_whenAccessingCity() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        // When
        String city = lead.contact().address().city();

        // Then
        assertThat(city).isEqualTo("New York");
    }
}