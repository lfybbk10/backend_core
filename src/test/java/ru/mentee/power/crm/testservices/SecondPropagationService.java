package ru.mentee.power.crm.testservices;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class SecondPropagationService {
  private final LeadRepository leadRepository;

  @Transactional(propagation = Propagation.REQUIRED)
  public void requiredTransaction(UUID leadId) {
    Lead lead = leadRepository.findById(leadId).orElseThrow();
    lead.setStatus("UPDATED_BY_SECOND");
    throw new RuntimeException("boom");
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void requiresNewTransaction(UUID leadId) {
    Lead lead = leadRepository.findById(leadId).orElseThrow();
    lead.setStatus("UPDATED_BY_SECOND");
    throw new RuntimeException("boom");
  }
}
