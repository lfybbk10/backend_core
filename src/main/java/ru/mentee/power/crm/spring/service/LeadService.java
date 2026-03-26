package ru.mentee.power.crm.spring.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.client.EmailValidationFeignClient;
import ru.mentee.power.crm.spring.client.EmailValidationResponse;
import ru.mentee.power.crm.spring.repository.LeadRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadService {
  private final LeadRepository repository;
  private final LeadProcessor processor;
  private final EmailValidationFeignClient emailValidationClient;

  @PostConstruct
  void init() {
    log.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  /**
   * Создаёт нового лида с проверкой уникальности email.
   *
   * @throws IllegalStateException если лид с таким email уже существует
   */
  public Lead addLead(String email, Company company, String status) {
    // Бизнес-правило: проверка уникальности email
    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    // Создаём нового лида
    Lead lead = new Lead(email, company, status);

    // Сохраняем через repository
    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  @Retry(name = "email-validation", fallbackMethod = "createLeadFallback")
  public Lead createLead(Lead lead) {
    log.info("createLead called");
    EmailValidationResponse validation = emailValidationClient.validateEmail(lead.getEmail());

    if (!validation.valid()) {
      throw new IllegalArgumentException("Invalid email: " + validation.reason());
    }

    lead.setCreatedAt(Instant.now());
    return repository.save(lead);
  }

  // Fallback метод — вызывается после исчерпания retry попыток
  public Lead createLeadFallback(Lead lead, Exception ex) {
    log.warn("Fallback called. Exception class = {}", ex.getClass().getName(), ex);

    log.warn(
        "Email validation service unavailable after retries. "
            + "Creating lead without validation. Error: {}",
        ex.getMessage());

    // Graceful degradation: создаём лида без валидации
    // В production можно: 1) пометить для последующей проверки
    //                     2) отправить в очередь на валидацию
    //                     3) отклонить запрос (throw new ServiceUnavailableException)
    lead.setCreatedAt(Instant.now());
    return repository.save(lead);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public List<Lead> findByStatus(String status) {
    return repository.findAll().stream()
        .filter(lead -> lead.getStatus().equals(status))
        .collect(Collectors.toList());
  }

  public List<Lead> findByStatuses(String... statuses) {
    return repository.findByStatusIn(List.of(statuses));
  }

  /** Получить первую страницу лидов с сортировкой. */
  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest =
        PageRequest.of(
            0, // первая страница (нумерация с 0)
            pageSize,
            Sort.by("createdAt").descending());
    return repository.findAll(pageRequest);
  }

  public Page<Lead> searchByCompany(String company, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByCompany(company, pageable);
  }

  /**
   * Массовое обновление статуса (используется @Modifying метод). ВАЖНО: @Transactional обязательна
   * для @Modifying!
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

  public List<Lead> findLeads(String search, String status) {
    List<Lead> leads = repository.findAll();
    Stream<Lead> searchStream =
        leads.stream()
            .filter(
                lead ->
                    search == null || lead.getEmail().toLowerCase().contains(search.toLowerCase()));
    Stream<Lead> statusStream =
        searchStream.filter(
            lead ->
                status == null || status.isEmpty() || lead.getStatus().equalsIgnoreCase(status));

    return statusStream.toList();
  }

  public Optional<Lead> update(UUID id, Lead updatedLead) {
    Optional<Lead> existing = repository.findById(id);
    if (!existing.isPresent()) {
      return Optional.empty();
    }

    existing.get().setEmail(updatedLead.getEmail());
    existing.get().setStatus(updatedLead.getStatus());
    existing.get().setCompany(updatedLead.getCompany());

    return Optional.of(repository.save(existing.get()));
  }

  public boolean delete(UUID id) {
    Optional<Lead> existing = repository.findById(id);
    if (existing.isPresent()) {
      repository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }

  @Transactional
  public void processLeads(List<Lead> leads) {
    for (Lead lead : leads) {
      processor.processSingleLead(lead.getId());
    }
  }
}
