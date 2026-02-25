package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LeadRepositoryTest {
    private LeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LeadRepository();
    }

    @Test
    void shouldSaveAndFindLeadById_whenLeadSaved() {
        Lead lead = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");
        repository.save(lead);
        assertThat(repository.findById("1")).isNotNull();
    }

    @Test
    void shouldReturnNull_whenLeadNotFound() {
        assertThat(repository.findById("1")).isNull();
    }

    @Test
    void shouldReturnAllLeads_whenMultipleLeadsSaved() {
        Lead lead1 = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");
        Lead lead2 = new Lead("2", "test@mail.com", "+79002314355", "Comp", "NEW");
        Lead lead3 = new Lead("3", "test@mail.com", "+79002314355", "Comp", "NEW");

        repository.save(lead1);
        repository.save(lead2);
        repository.save(lead3);

        assertThat(repository.size()).isEqualTo(3);
    }

    @Test
    void shouldDeleteLead_whenLeadExists() {
        Lead lead = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");
        repository.save(lead);
        repository.delete("1");

        assertThat(repository.findById("1")).isNull();
        assertThat(repository.size()).isEqualTo(0);
    }

    @Test
    void shouldOverwriteLead_whenSaveWithSameId() {
        Lead lead1 = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");
        Lead lead2 = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");

        repository.save(lead1);
        repository.save(lead2);
        assertThat(repository.findById("1")).isEqualTo(lead2);
        assertThat(repository.size()).isEqualTo(1);
    }

    @Test
    void shouldFindFasterWithMap_thanWithListFilter() {
        // Given: Создать 1000 лидов
        List<Lead> leadList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UUID id = UUID.randomUUID();
            Contact contact = new Contact(
                    "email" + i + "@test.com",
                    "+7" + i,
                    new Address("City" + i, "Street" + i, "ZIP" + i)
            );
            Lead lead = new Lead(id.toString(), contact.email(), "+79002314355", "Comp", "NEW");
            repository.save(lead);
            leadList.add(lead);
        }

        String targetId = "lead-500";  // Средний элемент

        // When: Поиск через Map
        long mapStart = System.nanoTime();
        Lead foundInMap = repository.findById(targetId);
        long mapDuration = System.nanoTime() - mapStart;

        // When: Поиск через List.stream().filter()
        long listStart = System.nanoTime();
        Lead foundInList = leadList.stream()
                .filter(lead -> lead.id().equals(targetId))
                .findFirst()
                .orElse(null);
        long listDuration = System.nanoTime() - listStart;

        // Then: Map должен быть минимум в 10 раз быстрее
        assertThat(foundInMap).isEqualTo(foundInList);
        assertThat(listDuration).isGreaterThan(mapDuration * 10);

        System.out.println("Map поиск: " + mapDuration + " ns");
        System.out.println("List поиск: " + listDuration + " ns");
        System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
    }

    @Test
    void shouldSaveBothLeads_evenWithSameEmailAndPhone_becauseRepositoryDoesNotCheckBusinessRules() {
        // Given: два лида с разными UUID но одинаковыми контактами
        Lead originalLead = new Lead("1", "test@mail.com", "+79002314355", "Comp", "NEW");
        Lead duplicateLead = new Lead("2", "test@mail.com", "+79002314355", "Comp", "NEW");

        // When: сохраняем оба
        repository.save(originalLead);
        repository.save(duplicateLead);

        // Then: Repository сохранил оба (это технически правильно!)
        assertThat(repository.size()).isEqualTo(2);

        // But: Бизнес недоволен — в CRM два контакта на одного человека
        // Решение: Service Layer в Sprint 5 будет проверять бизнес-правила
        // перед вызовом repository.save()
    }
}