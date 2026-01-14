package ru.mentee.power.crm.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class InMemoryLeadRepositoryTest {
    private InMemoryLeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
    }

    @Test
    void addTest(){
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        repository.save(lead);
        assertThat(repository.findAll().size()).isEqualTo(1);
        repository.save(lead);
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    void removeTest(){
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        repository.save(lead);
        assertThat(repository.findAll().size()).isEqualTo(1);
        repository.delete(randomUUID);
        assertThat(repository.findAll().size()).isEqualTo(0);
    }

    @Test
    void findByIdTest(){
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        repository.save(lead);
        assertThat(repository.findAll().size()).isEqualTo(1);
        assertThat(repository.findById(randomUUID).get()).isEqualTo(lead);
    }

    @Test
    void findAllTest(){
        Address address = new Address("New York", "Times Square", "123");
        Contact contact1 = new Contact("John@gmail.com", "+79122543221", address);
        Contact contact2 = new Contact("Test@gmail.com", "+79122213221", address);
        Lead lead1 = new Lead(UUID.randomUUID(), contact1, "TestCorp", "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), contact2, "TestCorp", "NEW");

        repository.save(lead1);
        repository.save(lead2);
        assertThat(repository.findAll().size()).isEqualTo(2);
    }
}
