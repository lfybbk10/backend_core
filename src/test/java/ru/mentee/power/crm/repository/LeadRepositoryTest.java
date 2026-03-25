package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

  @Autowired private LeadRepository repository;

  private Lead lead1;
  private Lead lead2;

  @BeforeEach
  void setUp() {
    // Подготовка тестовых данных
    lead1 = new Lead();
    lead1.setEmail("john@example.com");
    lead1.setStatus("NEW");
    lead1.setCreatedAt(Instant.now().minus(5, ChronoUnit.DAYS));
    repository.save(lead1);

    lead2 = new Lead();
    lead2.setEmail("jane@example.com");
    lead2.setStatus("CONTACTED");
    lead2.setCreatedAt(Instant.now().minus(2, ChronoUnit.DAYS));
    repository.save(lead2);
  }

  @Test
  void findByEmail_shouldReturnLead_whenExists() {
    // When
    Optional<Lead> found = repository.findByEmail("john@example.com");

    // Then
    assertThat(found).isPresent();
  }

  @Test
  void findByStatus_shouldReturnFilteredLeads() {
    // When
    List<Lead> newLeads = repository.findByStatus("NEW");

    // Then
    assertThat(newLeads).hasSize(1);
    assertThat(newLeads.get(0).getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void findByStatusIn_shouldReturnLeadsWithMultipleStatuses() {
    // Given
    List<String> statuses = List.of("NEW", "CONTACTED");

    // When
    List<Lead> found = repository.findByStatusIn(statuses);

    // Then
    assertThat(found).hasSize(2);
  }

  @Test
  void findAll_withPageable_shouldReturnPage() {
    // Given
    PageRequest pageRequest = PageRequest.of(0, 1);

    // When
    Page<Lead> page = repository.findAll(pageRequest);

    // Then
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getNumber()).isEqualTo(0); // текущая страница
  }

  // TODO: Студент добавляет тесты:
  // - для bulk операции updateStatusBulk (в отдельном тесте с @Modifying)

  @Test
  void countByStatus_shouldReturnCount() {
    Long count = repository.countByStatus("NEW");
    assertThat(count).isEqualTo(1L);

    count = repository.countByStatus("CONTACTED");
    assertThat(count).isEqualTo(1L);
  }

  @Test
  void existsByEmail_shouldReturnTrue_whenExists() {
    boolean existsByEmail = repository.existsByEmail("john@example.com");
    assertThat(existsByEmail).isTrue();

    existsByEmail = repository.existsByEmail("qwerty");
    assertThat(existsByEmail).isFalse();
  }

  @Test
  @Transactional
  void updateStatusBulkTest_shouldUpdateStatus() {
    int updated = repository.updateStatusBulk("NEW", "CONTACTED");
    assertThat(updated).isEqualTo(1);
    assertThat(repository.findByStatus("CONTACTED")).hasSize(2);
  }

  @Test
  void shouldFindByEmailIgnoreCase_whenExists() {
    Lead lead = new Lead();
    lead.setEmail("Test@Example.com");
    lead.setStatus("NEW");
    lead.setCreatedAt(Instant.now());

    System.out.println("before save company = " + (lead.getCompany() == null));
    repository.save(lead);
    System.out.println("after save company = " + (lead.getCompany() == null));

    Optional<Lead> leadFromRepository = repository.findByEmailIgnoreCase("test@example.com");

    assertThat(leadFromRepository).isPresent();
    assertThat(leadFromRepository.get().getEmail()).isEqualTo("Test@Example.com");
  }

  @Test
  void shouldReturnEmpty_whenEmailNotFound() {
    // When
    Optional<Lead> found = repository.findByEmailIgnoreCase("nonexistent@example.com");

    // Then
    assertThat(found).isEmpty();
  }
}
