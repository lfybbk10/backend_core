package ru.mentee.power.crm.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveCompanyWithLeads() {
        // Given
        Company company = new Company("Ozon", "Finance");

        Lead lead1 = new Lead("ivan@ozon.ru", "NEW");
        Lead lead2 = new Lead("maria@ozon.ru", "CONTACTED");
        Lead lead3 = new Lead("petr@ozon.ru", "CONTACTED");

        company.addLead(lead1);
        company.addLead(lead2);
        company.addLead(lead3);

        // When
        Company saved = companyRepository.save(company);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLeads()).hasSize(3);

        // Проверяем, что в БД создались записи
        Company found = companyRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getLeads()).hasSize(3);

        assertThat(leadRepository.findAll()).hasSize(3);
    }

    @Test
    void shouldAvoidN1WithEntityGraph() {
        // Given — создаём компанию с 5 лидами
        Company company = new Company("Тинькофф", "Finance");
        for (int i = 0; i < 5; i++) {
            company.addLead(new Lead("lead" + i + "@tinkoff.ru", "NEW"));
        }
        Company saved = companyRepository.save(company);

        // When — используем метод с @EntityGraph
        Company found = companyRepository.findByIdWithLeads(saved.getId()).orElseThrow();

        // Then — проверяем, что leads загружены
        assertThat(found.getLeads()).hasSize(5);

        // Проверьте SQL логи: должен быть 1 запрос с LEFT JOIN,
        // а не 1 SELECT для Company + 5 SELECT для каждого Lead
    }
}