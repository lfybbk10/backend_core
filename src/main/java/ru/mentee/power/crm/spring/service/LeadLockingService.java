package ru.mentee.power.crm.spring.service;

import static java.lang.Thread.sleep;

import jakarta.persistence.OptimisticLockException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@Service
public class LeadLockingService {
  private final LeadRepository leadRepository;

  public LeadLockingService(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  // Критическая операция с pessimistic lock
  @Transactional
  public Lead convertLeadToDealWithLock(UUID leadId, String newStatus) {
    // Блокируем Lead эксклюзивно до конца транзакции
    Lead lead =
        leadRepository
            .findByIdForUpdate(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Здесь могла бы быть сложная бизнес-логика конверсии:
    // - создание Deal
    // - обновление статуса Lead
    // - отправка уведомлений
    // Другие транзакции ЖДУТ завершения этой операции

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
  }

  // Обычное обновление с optimistic lock (через @Version)
  @Transactional
  public Lead updateLeadStatusOptimistic(UUID leadId, String newStatus) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Блокировки НЕТ — другие транзакции могут читать и изменять
    // При сохранении JPA проверит version и выбросит OptimisticLockException если конфликт

    lead.setStatus(newStatus);
    return leadRepository.save(lead);
    // UPDATE leads SET status=?, version=version+1 WHERE id=? AND version=?
  }

  @Transactional
  public Lead updateWithRetry(UUID leadId, String newStatus) {
    int attempts = 3;

    for (int i = 0; i < attempts; i++) {
      try {
        return updateLeadStatusOptimistic(leadId, newStatus);
      } catch (OptimisticLockException e) {
        System.out.println("Optimistic lock exception. Retry " + (i + 1));
      }
    }

    throw new RuntimeException("Failed to update lead after retries");
  }

  @Transactional
  public void lockTwoLeadsInOrder(UUID firstLeadId, UUID secondLeadId) throws InterruptedException {
    Lead first = leadRepository.findByIdForUpdate(firstLeadId).orElseThrow();
    sleep(200); // чтобы второй поток успел взять второй лок
    Lead second = leadRepository.findByIdForUpdate(secondLeadId).orElseThrow();

    first.setStatus("UPDATED");
    second.setStatus("UPDATED");
  }
}
