package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class LeadServiceTest {
    @Autowired
    private LeadService service;

    @Autowired
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Создаём 3 NEW лида
        for (int i = 1; i <= 3; i++) {
            Lead lead = new Lead();
            lead.setEmail("lead" + i + "@example.com");
            lead.setCompany("Company " + i);
            lead.setStatus("NEW");
            lead.setCreatedAt(LocalDateTime.now());
            repository.save(lead);
        }
    }

    @Test
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
    void archiveOldLeads_shouldUpdateMultipleLeads() {
        int updated = service.archiveOldLeads("NEW");
        assertThat(updated).isEqualTo(3);
        assertThat(repository.count()).isEqualTo(0);
    }
}