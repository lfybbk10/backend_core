package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;

@SpringBootTest
public class LeadServiceSelfInvocationTest {

  @Autowired private LeadService leadService;

  @Autowired private LeadRepository leadRepository;

  @Test
  @Disabled
  void demonstrateSelfInvocationProblem() {
    Lead lead1 = new Lead(null, "qwe@mail.ru", new Company("comp"), "NEW");
    Lead lead2 = new Lead(null, "qweQ@mail.ru", new Company("comp2"), "NEW");
    Lead lead3 = new Lead(UUID.randomUUID(), "qweQq@mail.ru", new Company("comp2"), "NEW");

    leadRepository.save(lead1);
    leadRepository.save(lead2);

    List<Lead> leads = Arrays.asList(lead1, lead2, lead3);
    try {
      leadService.processLeads(leads);
    } catch (Exception e) {
      System.out.println("Error in transaction");
    }

    assertThat(leadRepository.findByStatus("NEW").size()).isEqualTo(0);
  }
}
