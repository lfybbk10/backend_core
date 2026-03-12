package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.testservices.IsolationTestService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IsolationTest {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private IsolationTestService isolationTestService;

    @Test
    void isolation_REPEATABLE_READ_preventsNonRepeatableRead() throws Exception {
        Lead lead = new Lead(null, "john@mail.com", new Company("Comp"), "NEW");
        UUID leadId = leadRepository.save(lead).getId();

        CountDownLatch firstReadDone = new CountDownLatch(1);
        CountDownLatch updateDone = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<List<String>> txA = executor.submit(() ->
                    isolationTestService.readTwiceRepeatableRead(leadId, firstReadDone, updateDone)
            );

            Future<?> txB = executor.submit(() -> {
                try {
                    firstReadDone.await();
                    isolationTestService.updateEmail(leadId, "jane@mail.com");
                    updateDone.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });

            List<String> values = txA.get();
            txB.get();

            assertThat(values.get(0)).isEqualTo("john@mail.com");
            assertThat(values.get(1)).isEqualTo("john@mail.com");

            Lead actual = leadRepository.findById(leadId).orElseThrow();
            assertThat(actual.getEmail()).isEqualTo("jane@mail.com");
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void isolation_READ_COMMITTED_allowsNonRepeatableRead() throws Exception {
        Lead lead = new Lead(null, "john@mail.com", new Company("Comp"), "NEW");
        UUID leadId = leadRepository.save(lead).getId();

        CountDownLatch firstReadDone = new CountDownLatch(1);
        CountDownLatch updateDone = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<List<String>> txA = executor.submit(() ->
                    isolationTestService.readTwiceReadCommitted(leadId, firstReadDone, updateDone)
            );

            Future<?> txB = executor.submit(() -> {
                try {
                    firstReadDone.await();
                    isolationTestService.updateEmail(leadId, "jane@mail.com");
                    updateDone.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });

            List<String> values = txA.get();
            txB.get();

            assertThat(values.get(0)).isEqualTo("john@mail.com");
            assertThat(values.get(1)).isEqualTo("jane@mail.com");

            Lead actual = leadRepository.findById(leadId).orElseThrow();
            assertThat(actual.getEmail()).isEqualTo("jane@mail.com");
        } finally {
            executor.shutdown();
        }
    }
}