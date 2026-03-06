package ru.mentee.power.crm.spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.domain.Lead;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    /**
     * Поиск лида по email (точное совпадение).
     * SQL: SELECT * FROM leads WHERE email = ?
     */
    Optional<Lead> findByEmail(String email);

    /**
     * Поиск лидов по статусу.
     * SQL: SELECT * FROM leads WHERE status = ?
     */
    List<Lead> findByStatus(String status);

    List<Lead> findByCompany(String company);

    long countByStatus(String status);

    boolean existsByEmail(String email);

    /**
     * Поиск лидов по части email (LIKE запрос).
     * SQL: SELECT * FROM leads WHERE email LIKE '%emailPart%'
     */
    List<Lead> findByEmailContaining(String emailPart);

    List<Lead> findByStatusAndCompany(String status, String company);

    List<Lead> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Поиск лидов по списку статусов (JPQL).
     * JPQL: SELECT l FROM Lead l WHERE l.status IN :statuses
     * SQL: SELECT * FROM leads WHERE status IN (?, ?, ...)
     */
    @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
    List<Lead> findByStatusIn(@Param("statuses") List<String> statuses);

    @Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
    List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Поиск лидов с фильтрацией и сортировкой (JPQL).
     */
    @Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
    List<Lead> findByCompanyOrderedByDate(@Param("company") String company);

    @Query("SELECT l from Lead l WHERE l.company=:name")
    List<Lead> findByCompanyName(@Param("companyName") String name);

    // Методы с пагинацией

    /**
     * Поиск всех лидов с пагинацией (переопределяем из JpaRepository).
     * Клиент: PageRequest.of(0, 20) — первая страница, 20 элементов
     */
    Page<Lead> findAll(Pageable pageable);

    /**
     * Поиск по статусу с пагинацией (derived method).
     */
    Page<Lead> findByStatus(String status, Pageable pageable);

    Page<Lead> findByCompany(String company, Pageable pageable);

    /**
     * JPQL запрос с пагинацией.
     */
    @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
    Page<Lead> findByStatusInPaged(@Param("statuses") List<String> statuses, Pageable pageable);

    // Bulk операции

    /**
     * Массовое обновление статуса лидов.
     * ВАЖНО: требует @Transactional на уровне Service!
     *
     * @return количество обновлённых строк
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
    int updateStatusBulk(
            @Param("oldStatus") String oldStatus,
            @Param("newStatus") String newStatus
    );


    @Modifying
    @Query("DELETE FROM Lead l WHERE l.status=:status")
    int deleteByStatusBulk(@Param("status") String status);
}