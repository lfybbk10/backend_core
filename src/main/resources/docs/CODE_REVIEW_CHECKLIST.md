# REST Controller Code Review Checklist

## Категория 1: API Design (5 проблем)

### 1.1 Неправильные HTTP методы
**Приоритет:** CRITICAL  
**Что искать:** POST используется для чтения данных, GET для модификации

**Плохо:**
```java
@PostMapping("/getInvitees") // Глагол в URL + неправильный метод
public List<Invitee> getInvitees() { ... }
```

**Хорошо:**
```java
@GetMapping("/invitees") // Существительное + правильный HTTP метод
public ResponseEntity<List<InviteeResponse>> getInvitees() { ... }
```

---

### 1.2 Неправильные статус коды
**Приоритет:** CRITICAL  
**Что искать:** 200 для всех операций, отсутствие 201/204/404

**Плохо:**
```java
@PostMapping("/invitees")
public Invitee create(@RequestBody Invitee invitee) {
    return service.save(invitee); // Всегда 200 OK
}
```

**Хорошо:**
```java
@PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = service.create(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created); // 201 Created + Location header
}
```

---

### 1.3 Плохой naming: глаголы в URL
**Приоритет:** MAJOR  
**Что искать:** /getInvitees, /createInvitee, /updateInviteeStatus

**Плохо:**
```java
@GetMapping("/getInvitees") // RPC стиль
@PostMapping("/createInvitee")
```

**Хорошо:**
```java
@GetMapping("/invitees") // RESTful стиль
@PostMapping("/invitees")
```

---

### 1.4 Entity вместо DTO в response
**Приоритет:** CRITICAL (security + coupling)

**Плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
    return repository.findById(id).orElseThrow(); // Entity с JPA annotations, internal fields
}
```

**Хорошо:**
```java
@GetMapping("/invitees/{id}")
public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    Invitee invitee = service.getById(id);
    return ResponseEntity.ok(mapper.toResponse(invitee)); // DTO без internal fields
}
```

---

### 1.5 Нет пагинации для списков
**Приоритет:** MAJOR (performance)

**Плохо:**
```java
@GetMapping("/invitees")
public List<Invitee> getAll() {
    return repository.findAll(); // Может вернуть 10,000 записей
}
```

**Хорошо:**
```java
@GetMapping("/invitees")
public ResponseEntity<Page<InviteeResponse>> getAll(
@PageableDefault(size = 20) Pageable pageable) {
    Page<Invitee> page = repository.findAll(pageable);
    return ResponseEntity.ok(page.map(mapper::toResponse));
}
```

---

## Категория 2: Security (5 проблем)

### 2.1 SQL injection через конкатенацию
**Приоритет:** CRITICAL

**Плохо:**
```java
String sql = "SELECT * FROM invitees WHERE email = '" + email + "'";
// Injection: email = "admin' OR '1'='1"
```

**Хорошо:**
```java
// Spring Data JPA method
Invitee findByEmail(String email); // Автоматическое экранирование

// Или PreparedStatement
PreparedStatement ps = conn.prepareStatement("SELECT * FROM invitees WHERE email = ?");
ps.setString(1, email);
```

---

### 2.2 Exposure внутренних полей
**Приоритет:** CRITICAL

**Плохо:**
```java
@Data
@Entity
public class User {
    private UUID id;
    private String email;
    private String password; // НИКОГДА не должно попасть в response
    private String internalSystemId; // Internal field
    @Version private Long version; // JPA optimistic locking
}

@GetMapping("/users/{id}")
public User getUser(@PathVariable UUID id) {
    return userRepo.findById(id).orElseThrow(); // Вернёт ВСЕ поля включая password
}
```

**Хорошо:**
```java
public record UserResponse(UUID id, String email, String firstName) {} // Только публичные поля

@GetMapping("/users/{id}")
public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
    User user = userService.getById(id);
    return ResponseEntity.ok(userMapper.toResponse(user)); // password не попадёт в JSON
}
```

---

### 2.3 Нет валидации входных данных
**Приоритет:** CRITICAL

**Плохо:**
```java
@PostMapping("/invitees")
public Invitee create(@RequestBody Invitee invitee) { // Нет @Valid
    return service.save(invitee); // Любые данные принимаются
}
```

**Хорошо:**
```java
public record CreateInviteeRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 2, max = 50) String firstName
) {}

@PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    // Spring автоматически валидирует, выбрасывает MethodArgumentNotValidException если ошибка
    InviteeResponse created = service.create(request);
    return ResponseEntity.created(location).body(created);
}
```

---

### 2.4 Stack trace в error response
**Приоритет:** CRITICAL

**Плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
    try {
        return repository.findById(id).orElseThrow();
    } catch (Exception e) {
        e.printStackTrace(); // Stack trace в logs OK, но...
        throw e; // Default Spring response включает stack trace в JSON для клиента
    }
}
```

**Хорошо:**
```java
// GlobalExceptionHandler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex) {
        logger.error("Entity not found", ex); // Full stack trace в server logs
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            "Resource not found" // Generic message для клиента, NO stack trace
        );
        return ResponseEntity.status(404).body(problem);
    }
}
```

---

### 2.5 Missing authorization checks
**Приоритет:** CRITICAL

**Плохо:**
```java
@DeleteMapping("/invitees/{id}")
public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id); // Любой авторизованный пользователь может удалить любого invitee
    return ResponseEntity.noContent().build();
}
```

**Хорошо:**
```java
@DeleteMapping("/invitees/{id}")
@PreAuthorize("hasRole('ADMIN') or @inviteeService.isOwner(#id, authentication.name)")
public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id); // Только ADMIN или owner может удалить
    return ResponseEntity.noContent().build();
}
```

---

## Категория 3: Error Handling (4 проблемы)

### 3.1 Пустые catch блоки
**Приоритет:** MAJOR

**Плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
    try {
        return repository.findById(id).orElseThrow();
    } catch (Exception e) {
        // TODO: handle
        return null; // Клиент получит null вместо error response
    }
}
```

**Хорошо:**
```java
@GetMapping("/invitees/{id}")
public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    Invitee invitee = service.getById(id); // Service выбросит EntityNotFoundException
    return ResponseEntity.ok(mapper.toResponse(invitee));
    // GlobalExceptionHandler перехватит exception, вернёт 404 с Problem Details
}
```

---

### 3.2 500 на бизнес-ошибки вместо 4xx
**Приоритет:** MAJOR

**Плохо:**
```java
@PostMapping("/invitees")
public Invitee create(@RequestBody Invitee invitee) {
    if (repository.existsByEmail(invitee.getEmail())) {
        throw new RuntimeException("Email exists"); // Default Spring: 500 Internal Server Error
    }
    return repository.save(invitee);
}
```

**Хорошо:**
```java
// Custom exception
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}

// В Service
if (repository.existsByEmail(request.email())) {
    throw new EmailAlreadyExistsException(request.email());
}

// GlobalExceptionHandler
@ExceptionHandler(EmailAlreadyExistsException.class)
public ResponseEntity<ProblemDetail> handleEmailExists(EmailAlreadyExistsException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, // 409 Conflict (бизнес-ошибка, не server error)
        ex.getMessage()
    );
    return ResponseEntity.status(409).body(problem);
}
```

---

### 3.3 Generic error messages без деталей
**Приоритет:** MINOR

**Плохо:**
```json
{
  "error": "Error occurred"
}
```

**Хорошо:**
```json
{
  "type": "/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed for 2 fields",
  "instance": "/api/invitees",
  "errors": {
    "email": "Email is required and must be valid",
    "firstName": "First name must be between 2 and 50 characters"
  }
}
```

---

### 3.4 Нет логирования ошибок
**Приоритет:** MAJOR

**Плохо:**
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    return ResponseEntity.status(500).body(new ErrorResponse("Internal error"));
    // Exception потерян, нет способа debug в production
}
```

**Хорошо:**
```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
    logger.error("Unexpected error for request: {} {}", request.getMethod(), request.getRequestURI(), ex);

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred"
    );
    return ResponseEntity.status(500).body(problem);
}
```

---

## Категория 4: Code Quality (4 проблемы)

### 4.1 Бизнес-логика в контроллере
**Приоритет:** MAJOR (violates SRP)

**Плохо:**
```java
@PostMapping("/invitees")
public ResponseEntity<Invitee> create(@RequestBody Invitee invitee) {

    if (repository.existsByEmail(invitee.getEmail())) {
        throw new EmailAlreadyExistsException(invitee.getEmail());
    }

    if (invitee.getFirstName().length() < 2) {
        throw new ValidationException("Name too short");
    }

    invitee.setCreatedAt(Instant.now());
    invitee.setStatus(InviteeStatus.NEW);

    Invitee saved = repository.save(invitee);
    emailService.sendWelcomeEmail(saved.getEmail());

    return ResponseEntity.created(location).body(saved);
}
```

**Хорошо:**
```java
@PostMapping("/invitees")
public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = inviteeService.create(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created);
}

// Service
public InviteeResponse create(CreateInviteeRequest request) {
    validateEmailUnique(request.email());
    Invitee invitee = buildNewInvitee(request);
    Invitee saved = repository.save(invitee);
    emailService.sendWelcomeEmail(saved.getEmail());
    return mapper.toResponse(saved);
}
```

---

### 4.2 Дублирование кода
**Приоритет:** MAJOR

**Плохо:**
```java
@GetMapping("/invitees/{id}")
public Invitee getById(@PathVariable UUID id) {
    try {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Invitee not found"));
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    }
}

@GetMapping("/deals/{id}")
public Deal getDealById(@PathVariable UUID id) {
    try {
        return dealRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Deal not found"));
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
    }
}
```

**Хорошо:**
```java
@GetMapping("/invitees/{id}")
public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    Invitee invitee = service.getById(id);
    return ResponseEntity.ok(mapper.toResponse(invitee));
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(404).body(problem);
    }
}
```

---

### 4.3 God Controller
**Приоритет:** MINOR

**Плохо:**
```java
@RestController
@RequestMapping("/invitees")
public class InviteeController {

    @GetMapping public List<Invitee> getAll() {}
    @GetMapping("/{id}") public Invitee getById() {}
    @PostMapping public Invitee create() {}
    @PutMapping("/{id}") public Invitee update() {}
    @DeleteMapping("/{id}") public void delete() {}

    @PostMapping("/{id}/convert") public Deal convertToDeal() {}

    @PostMapping("/{id}/send-welcome") public void sendWelcome() {}
    @PostMapping("/{id}/send-reminder") public void sendReminder() {}

    @GetMapping("/report/monthly") public Report getMonthly() {}
    @GetMapping("/report/by-status") public Report getByStatus() {}

    @PostMapping("/import") public void importCsv() {}
    @GetMapping("/export") public byte[] exportExcel() {}
}
```

**Хорошо:**
```java
@RestController
@RequestMapping("/invitees")
public class InviteeController {
}

@RestController
@RequestMapping("/invitees/{inviteeId}/conversion")
public class InviteeConversionController {
}

@RestController
@RequestMapping("/invitees/{inviteeId}/notifications")
public class InviteeNotificationController {
}

@RestController
@RequestMapping("/reports/invitees")
public class InviteeReportController {
}
```

---

### 4.4 Hardcoded values
**Приоритет:** MINOR

**Плохо:**
```java
@PreAuthorize("hasRole('ROLE_ADMIN')")
String apiUrl = "https://api.example.com/send-email";
```

**Хорошо:**
```yaml
app:
  roles:
    admin: ROLE_ADMIN
```

```java
@PreAuthorize("hasRole(@appProperties.roles().admin())")
```
