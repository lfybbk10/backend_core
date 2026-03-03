package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

    @Autowired
    private LeadRepository repository;

    @Test
    void shouldSaveAndFindLeadById_whenValidData() {
        // Given
        Lead lead = new Lead(null, "test@example.com", "ACME", "NEW");

        // When
        Lead saved = repository.save(lead);
        Optional<Lead> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByEmailNative_whenLeadExists() {
        // Given
        Lead lead = new Lead(null, "native@test.com", "TechCorp", "NEW");
        repository.save(lead);

        // When
        Optional<Lead> found = repository.findByEmailNative("native@test.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCompany()).isEqualTo("TechCorp");
    }

    @Test
    void shouldReturnEmptyOptional_whenEmailNotFound() {
        // When
        Optional<Lead> found = repository.findByEmailNative("nonexistent@test.com");

        // Then
        assertThat(found).isEmpty();
    }

    // TODO: студент добавляет тесты для findAll, delete
    @Test
    void shouldReturnCorrectCountWithFindAll(){
        Lead lead1 = new Lead(null, "test@example.com", "ACME", "NEW");
        Lead lead2 = new Lead(null, "test1@example.com", "ACM", "NEW");

        repository.save(lead1);
        repository.save(lead2);
        assertThat(repository.findAll().size()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyListAfterDelete(){
        Lead lead = new Lead(null, "native@test.com", "TechCorp", "NEW");
        repository.save(lead);

        repository.deleteById(lead.getId());

        assertThat(repository.findAll()).isEmpty();
    }
}