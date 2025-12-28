package ru.mentee.power.crm.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LeadStorageTest {

    @Test
    void shouldAddLead_whenLeadIsUnique() {
        // Given
        LeadStorage storage = new LeadStorage();
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead uniqueLead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // When
        boolean added = storage.add(uniqueLead);

        // Then
        assertThat(added).isTrue();
        assertThat(storage.size()).isEqualTo(1);
        assertThat(storage.findAll()).containsExactly(uniqueLead);
    }

    @Test
    void shouldRejectDuplicate_whenEmailAlreadyExists() {
        // Given
        LeadStorage storage = new LeadStorage();
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead existingLead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead duplicateLead = new Lead(randomUUID, contact, "TestCorp", "NEW");
        storage.add(existingLead);

        // When
        boolean added = storage.add(duplicateLead);

        // Then
        assertThat(added).isFalse();
        assertThat(storage.size()).isEqualTo(1);
        assertThat(storage.findAll()).containsExactly(existingLead);
    }

    @Test
    void shouldThrowException_whenStorageIsFull() {
        // Given: Заполни хранилище 100 лидами
        LeadStorage storage = new LeadStorage();
        for (int index = 0; index < 100; index++) {
            UUID randomUUID = UUID.randomUUID();
            Address address = new Address("New York", "Times Square", "123");
            Contact contact = new Contact("John@gmail.com", "+79122543221", address);
            Lead uniqueLead = new Lead(randomUUID, contact, "TestCorp", "NEW");
            storage.add(uniqueLead);
        }

        // When + Then: 101-й лид должен выбросить исключение
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead newLead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        assertThatThrownBy(() -> storage.add(newLead))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Storage is full");
    }

    @Test
    void shouldReturnOnlyAddedLeads_whenFindAllCalled() {
        // Given
        LeadStorage storage = new LeadStorage();

        Address address = new Address("New York", "Times Square", "123");
        Contact contact1 = new Contact("John@gmail.com", "+79122543221", address);
        Contact contact2 = new Contact("Test@gmail.com", "+79122213221", address);
        Lead lead1 = new Lead(UUID.randomUUID(), contact1, "TestCorp", "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), contact2, "TestCorp", "NEW");
        storage.add(lead1);
        storage.add(lead2);

        // When
        Lead[] result = storage.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(lead1, lead2);
    }
}