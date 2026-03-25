package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.DealJpaRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.DealService;

@SpringBootTest
public class DealServiceIntegrationTest {

  @Autowired private DealService dealService;

  @Autowired private DealJpaRepository dealRepository;

  @Autowired private LeadRepository leadRepository;

  @Test
  @Disabled
  void convertLeadToDeal_shouldRollbackOnConstraintViolation() {
    Lead lead = new Lead(null, "asdasd@mail.ru", new Company("comp"), "NEW");
    leadRepository.save(lead);

    assertThrows(
        NullPointerException.class,
        () -> {
          dealService.convertLeadToDeal(lead.getId(), null);
        });

    Lead savedLead = leadRepository.findById(lead.getId()).orElseThrow();
    assertThat(savedLead.getStatus()).isEqualTo("NEW");

    assertThat(dealRepository.findAll().size()).isEqualTo(0);
  }
}
