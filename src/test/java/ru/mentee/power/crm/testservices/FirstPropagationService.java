package ru.mentee.power.crm.testservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirstPropagationService {
    private final LeadRepository leadRepository;
    private final SecondPropagationService secondPropagationService;

    @Transactional
    public void requiredTransaction(UUID leadId) {
        Lead lead = leadRepository.findById(leadId).orElseThrow();
        lead.setStatus("UPDATED_BY_FIRST");

        secondPropagationService.requiredTransaction(leadId);
    }

    @Transactional
    public void requiresNewTransaction(UUID lead1Id, UUID lead2Id) {
        Lead lead = leadRepository.findById(lead1Id).orElseThrow();
        lead.setStatus("UPDATED_BY_FIRST");

        try {
            secondPropagationService.requiresNewTransaction(lead2Id);
        } catch (RuntimeException e) {
            System.out.println("Error in second transaction");
        }
    }

}
