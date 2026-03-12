package ru.mentee.power.crm.spring.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadService {
    private final LeadRepository repository;
    private final LeadProcessor processor;

    @PostConstruct
    void init() {
        log.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
    }

    /**
     * Создаёт нового лида с проверкой уникальности email.
     *
     * @throws IllegalStateException если лид с таким email уже существует
     */
    public Lead addLead(String email, String company, String status) {
        // Бизнес-правило: проверка уникальности email
        Optional<Lead> existing = repository.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }

        // Создаём нового лида
        Lead lead = new Lead(
                UUID.randomUUID(),
                email,
                company,
                status
        );

        // Сохраняем через repository
        return repository.save(lead);
    }

    public List<Lead> findAll() {
        return repository.findAll();
    }

    public Optional<Lead> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Lead> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Lead> findByStatus(String status) {
        return repository.findAll().stream().filter(lead -> lead.getStatus().equals(status)).collect(Collectors.toList());
    }

    public List<Lead> findByStatuses(String... statuses) {
        return repository.findByStatusIn(List.of(statuses));
    }

    /**
     * Получить первую страницу лидов с сортировкой.
     */
    public Page<Lead> getFirstPage(int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                0, // первая страница (нумерация с 0)
                pageSize,
                Sort.by("createdAt").descending()
        );
        return repository.findAll(pageRequest);
    }

     public Page<Lead> searchByCompany(String company, int page, int size) {
       Pageable pageable = PageRequest.of(page, size);
       return repository.findByCompany(company, pageable);
     }

    /**
     * Массовое обновление статуса (используется @Modifying метод).
     * ВАЖНО: @Transactional обязательна для @Modifying!
     */
    @Transactional
    public int convertNewToContacted() {
        int updated = repository.updateStatusBulk("NEW", "CONTACTED");
        // Логируем для observability
        System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
        return updated;
    }

     @Transactional
     public int archiveOldLeads(String status) {
       return repository.deleteByStatusBulk(status);
     }

    public List<Lead> findLeads(String search, String status){
        List<Lead> leads = repository.findAll();
        Stream<Lead> searchStream = leads.stream().filter(lead ->
                search == null || lead.getEmail().toLowerCase().contains(search.toLowerCase()));
        Stream<Lead> statusStream = searchStream.filter(lead ->
                status == null || status.isEmpty() || lead.getStatus().equalsIgnoreCase(status));

        return statusStream.toList();
    }

    public Lead update(UUID id, Lead updatedLead){
        Optional<Lead> existing = repository.findById(id);
        if (existing.isPresent()) {
            return repository.save(updatedLead);
        }
        else{
            throw new IllegalStateException("Lead with id " + id + " not found");
        }
    }

    public void delete(UUID id) {
        Optional<Lead> existing = repository.findById(id);
        if (existing.isPresent()) {
            repository.deleteById(id);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with id " + id + " not found");
        }
    }

    @Transactional
    public void processLeads(List<Lead> leads) {
        for (Lead lead : leads) {
            processor.processSingleLead(lead.getId());
        }
    }
}