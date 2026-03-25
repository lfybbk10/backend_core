package ru.mentee.power.crm.testservices;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class IsolationTestService {

  private final LeadRepository leadRepository;

  @PersistenceContext private EntityManager entityManager;

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<String> readTwiceRepeatableRead(
      UUID leadId, CountDownLatch firstReadDone, CountDownLatch updateDone) {
    Lead firstLead = leadRepository.findById(leadId).orElseThrow();
    String firstValue = firstLead.getEmail();

    firstReadDone.countDown();

    try {
      updateDone.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }

    entityManager.clear();

    Lead secondLead = leadRepository.findById(leadId).orElseThrow();
    String secondValue = secondLead.getEmail();

    return List.of(firstValue, secondValue);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public List<String> readTwiceReadCommitted(
      UUID leadId, CountDownLatch firstReadDone, CountDownLatch updateDone) {
    Lead firstLead = leadRepository.findById(leadId).orElseThrow();
    String firstValue = firstLead.getEmail();

    firstReadDone.countDown();

    try {
      updateDone.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }

    entityManager.clear();

    Lead secondLead = leadRepository.findById(leadId).orElseThrow();
    String secondValue = secondLead.getEmail();

    return List.of(firstValue, secondValue);
  }

  @Transactional
  public void updateEmail(UUID leadId, String newEmail) {
    Lead lead = leadRepository.findById(leadId).orElseThrow();
    lead.setEmail(newEmail);
  }
}
