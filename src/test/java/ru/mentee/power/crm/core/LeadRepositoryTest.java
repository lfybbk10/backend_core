package ru.mentee.power.crm.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

class LeadRepositoryTest {

    @Test
    @DisplayName("Should automatically deduplicate leads by id")
    void shouldDeduplicateLeadsById() {
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        LeadRepository repo = new LeadRepository();
        repo.add(lead);

        boolean addResult = repo.add(lead);
        assertThat(repo.size()).isEqualTo(1);
        assertThat(addResult).isFalse();
    }

    @Test
    @DisplayName("Should allow different leads with different ids")
    void shouldAllowDifferentLeads() {
        Address address = new Address("New York", "Times Square", "123");
        Contact contact1 = new Contact("John@gmail.com", "+79122543221", address);
        Contact contact2 = new Contact("Test@gmail.com", "+79122213221", address);
        Lead lead1 = new Lead(UUID.randomUUID(), contact1, "TestCorp", "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), contact2, "TestCorp", "NEW");

        LeadRepository repo = new LeadRepository();
        assertThat(repo.add(lead1)).isTrue();
        assertThat(repo.add(lead2)).isTrue();
        assertThat(repo.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find existing lead through contains")
    void shouldFindExistingLead() {
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        LeadRepository repo = new LeadRepository();
        repo.add(lead);

        assertThat(repo.contains(lead)).isTrue();
    }

    @Test
    @DisplayName("Should return unmodifiable set from findAll")
    void shouldReturnUnmodifiableSet() {
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        LeadRepository repo = new LeadRepository();
        repo.add(lead);
        assertThatThrownBy(() -> repo.findAll().remove(lead)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should perform contains() faster than ArrayList")
    void shouldPerformFasterThanArrayList() {
        HashSet<Lead> leadSet = new HashSet<>();
        ArrayList<Lead> leadList = new ArrayList<>();

        for (int i = 0; i<10000; i++) {
            UUID randomUUID = UUID.randomUUID();
            Address address = new Address("New York", "Times Square", "123");
            Contact contact = new Contact("John@gmail.com", "+79122543221", address);
            Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
            leadSet.add(lead);
            leadList.add(lead);
        }

        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++){
            UUID randomUUID = UUID.randomUUID();
            Address address = new Address("New York", "Times Square", "123");
            Contact contact = new Contact("John@gmail.com", "+79122543221", address);
            Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
            leadSet.contains(lead);
        }
        long duration = System.nanoTime() - start;
        System.out.println("Время вызова 1000 contains на HashSet 10000 элементов: "+duration);

        start = System.nanoTime();
        for (int i = 0; i < 1000; i++){
            UUID randomUUID = UUID.randomUUID();
            Address address = new Address("New York", "Times Square", "123");
            Contact contact = new Contact("John@gmail.com", "+79122543221", address);
            Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");
            leadList.contains(lead);
        }
        duration = System.nanoTime() - start;
        System.out.println("Время вызова 1000 contains на ArrayList 10000 элементов: "+duration);
    }
}