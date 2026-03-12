package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadLockingService;

import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class LeadLockingServiceTest {

    @Autowired
    private LeadLockingService leadLockingService;

    @Autowired
    private LeadRepository leadRepository;

    @Test
    void shouldPreventLostUpdate_whenPessimisticLockUsed() throws Exception {
        // Given: Lead с начальным статусом
        Lead lead = new Lead(null, "concurrent@test.com", "comp","NEW");
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют Lead с pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Future<String> task1 = executor.submit(() -> {
            startLatch.await(); // Синхронизируем старт
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, "CONTACTED");
            doneLatch.countDown();
            return updated.getStatus();
        });

        Future<String> task2 = executor.submit(() -> {
            startLatch.await();
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, "QUALIFIED");
            doneLatch.countDown();
            return updated.getStatus();
        });

        startLatch.countDown(); // Запускаем оба потока одновременно
        doneLatch.await(10, TimeUnit.SECONDS); // Ждём завершения

        // Then: Оба обновления успешны, вторая транзакция ждала первую
        String status1 = task1.get();
        String status2 = task2.get();

        assertThat(status1).isIn("CONTACTED", "QUALIFIED");
        assertThat(status2).isIn("CONTACTED", "QUALIFIED");
        assertThat(status1).isNotEqualTo(status2); // Разные статусы (не должны быть)

        // Финальный статус — последняя commit'нутая транзакция
        Lead finalLead = leadRepository.findById(leadId).orElseThrow();
        assertThat(finalLead.getStatus()).isIn("CONTACTED", "QUALIFIED");

        executor.shutdown();
    }

    @Test
    void shouldThrowOptimisticLockException_whenConcurrentUpdateWithoutLock() throws Exception {
        // Given: Lead с optimistic locking через @Version
        Lead lead = new Lead(null, "optimistic@test.com", "comp","NEW");
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют БЕЗ pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> task1 = executor.submit(() -> {
            startLatch.await();
            leadLockingService.updateLeadStatusOptimistic(leadId, "CONTACTED");
            return null;
        });

        Future<?> task2 = executor.submit(() -> {
            startLatch.await();
            Thread.sleep(50); // Небольшая задержка чтобы первая транзакция стартовала
            leadLockingService.updateLeadStatusOptimistic(leadId, "QUALIFIED");
            return null;
        });

        startLatch.countDown();

        // Then: Одна транзакция успешна, вторая выбрасывает OptimisticLockException
        boolean exceptionThrown = false;
        try {
            task1.get(5, TimeUnit.SECONDS);
            task2.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // Одна из транзакций должна выбросить OptimisticLockException
            assertThat(e.getCause())
                    .isInstanceOfAny(ObjectOptimisticLockingFailureException.class);
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isTrue();
        executor.shutdown();
    }

    @Test
    void shouldThrowCannotAcquireLockException_whenDeadlockOccurs() throws Exception {
        // Given
        Lead lead1 = new Lead(null, "deadlock1@test.com", "comp1", "NEW");
        Lead lead2 = new Lead(null, "deadlock2@test.com", "comp2", "NEW");

        lead1 = leadRepository.save(lead1);
        lead2 = leadRepository.save(lead2);

        UUID leadId1 = lead1.getId();
        UUID leadId2 = lead2.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> task1 = executor.submit(() -> {
            startLatch.await();
            leadLockingService.lockTwoLeadsInOrder(leadId1, leadId2);
            return null;
        });

        Future<?> task2 = executor.submit(() -> {
            startLatch.await();
            leadLockingService.lockTwoLeadsInOrder(leadId2, leadId1);
            return null;
        });

        // When
        startLatch.countDown();

        // Then
        Throwable firstError = null;
        Throwable secondError = null;

        try {
            task1.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            firstError = e.getCause();
        }

        try {
            task2.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            secondError = e.getCause();
        }

        assertThat(firstError != null || secondError != null).isTrue();

        Throwable error = firstError != null ? firstError : secondError;

        assertThat(error)
                .isInstanceOfAny(
                        org.springframework.dao.CannotAcquireLockException.class,
                        org.springframework.dao.PessimisticLockingFailureException.class,
                        jakarta.persistence.PessimisticLockException.class
                );

        executor.shutdown();
    }
}