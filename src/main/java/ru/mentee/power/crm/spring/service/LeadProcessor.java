package ru.mentee.power.crm.spring.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@Service
public class LeadProcessor {
  @Autowired private LeadRepository leadRepository;

  @Transactional(propagation = REQUIRES_NEW)
  public void processSingleLead(UUID id) {
    leadRepository.findById(id).get().setStatus("PROCESSED");
  }
}
