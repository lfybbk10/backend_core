package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.testservices.FirstPropagationService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PropagationTest {

    @Autowired
    private FirstPropagationService firstPropagationService;

    @Autowired
    private LeadRepository leadRepository;

    @Test
    void propagation_REQUIRED_shouldReuseTransaction() {
        Lead lead = new Lead(null, "john@mail.com", "Comp", "NEW");
        lead = leadRepository.save(lead);

        Lead finalLead = lead;
        assertThrows(RuntimeException.class, () -> {
            firstPropagationService.requiredTransaction(finalLead.getId());
        });

        Lead actual = leadRepository.findById(lead.getId()).orElseThrow();

        assertThat(actual.getEmail()).isEqualTo("john@mail.com");
        assertThat(actual.getStatus()).isEqualTo("NEW");
    }

    @Test
    void propagation_REQUIRES_NEW_shouldCreateNewTransaction() {
        Lead lead1 = new Lead(null, "john@mail.com", "Comp", "NEW");
        UUID lead1Id = leadRepository.save(lead1).getId();

        Lead lead2 = new Lead(null, "jack@mail.com", "Comp", "NEW");
        UUID lead2Id = leadRepository.save(lead2).getId();

        firstPropagationService.requiresNewTransaction(lead1Id, lead2Id);

        Lead actual1 = leadRepository.findById(lead1Id).orElseThrow();
        Lead actual2 = leadRepository.findById(lead2Id).orElseThrow();

        assertThat(actual1.getEmail()).isEqualTo("john@mail.com");
        assertThat(actual1.getStatus()).isEqualTo("UPDATED_BY_FIRST");

        assertThat(actual2.getEmail()).isEqualTo("jack@mail.com");
        assertThat(actual2.getStatus()).isEqualTo("NEW");
    }
}
