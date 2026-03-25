package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

@SpringBootTest
@Transactional
class LeadServiceTest {
  @Autowired private LeadService service;

  @Autowired private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    // Создаём 3 NEW лида
    for (int i = 1; i <= 3; i++) {
      Lead lead = new Lead();
      lead.setEmail("lead" + i + "@example.com");
      lead.setCompany(new Company("Company " + i));
      lead.setStatus("NEW");
      lead.setCreatedAt(Instant.now());
      repository.save(lead);
    }
  }

  @Test
  @Disabled
  void convertNewToContacted_shouldUpdateMultipleLeads() {
    // When
    int updated = service.convertNewToContacted();

    // Then
    assertThat(updated).isEqualTo(3);

    // Проверяем что статус изменился
    long contactedCount = repository.countByStatus("CONTACTED");
    assertThat(contactedCount).isEqualTo(3);

    long newCount = repository.countByStatus("NEW");
    assertThat(newCount).isEqualTo(0);
  }

  @Test
  @Disabled
  void archiveOldLeads_shouldUpdateMultipleLeads() {
    int updated = service.archiveOldLeads("NEW");
    assertThat(updated).isEqualTo(3);
    assertThat(repository.count()).isEqualTo(0);
  }
}
