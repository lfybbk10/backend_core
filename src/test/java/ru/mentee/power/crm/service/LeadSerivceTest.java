package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.infrastructure.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class LeadServiceTest {

    private LeadService service;
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
        service = new LeadService(repository);
    }

    @Test
    void shouldCreateLead_whenEmailIsUnique() {
        // Given
        String email = "test@example.com";
        String company = "Test Company";

        // When
        Lead result = service.addLead(email, company, "NEW");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.contact().email()).isEqualTo(email);
        assertThat(result.company()).isEqualTo(company);
        assertThat(result.status()).isEqualTo("NEW");
        assertThat(result.id()).isNotNull();
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {
        // Given
        String email = "duplicate@example.com";
        service.addLead(email, "First Company", "NEW");

        // When/Then
        assertThatThrownBy(() ->
                service.addLead(email, "Second Company", "NEW")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Lead with email already exists");
    }

    @Test
    void shouldFindAllLeads() {
        // Given
        service.addLead("one@example.com", "Company 1", "NEW");
        service.addLead("two@example.com", "Company 2", "CONVERTED");

        // When
        List<Lead> result = service.findAll();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFindLeadById() {
        // Given
        Lead created = service.addLead("find@example.com", "Company", "NEW");

        // When
        Optional<Lead> result = service.findById(created.id());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().contact().email()).isEqualTo("find@example.com");
    }

    @Test
    void shouldFindLeadByEmail() {
        // Given
        service.addLead("search@example.com", "Company", "NEW");

        // When
        Optional<Lead> result = service.findByEmail("search@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().company()).isEqualTo("Company");
    }

    @Test
    void shouldReturnEmpty_whenLeadNotFound() {
        // Given/When
        Optional<Lead> result = service.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }
}