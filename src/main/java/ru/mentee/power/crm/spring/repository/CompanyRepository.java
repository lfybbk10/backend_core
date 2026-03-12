package ru.mentee.power.crm.spring.repository;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.domain.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @EntityGraph(attributePaths = {"leads"})
    @Query("SELECT c FROM Company c WHERE c.id = :id")
    Optional<Company> findByIdWithLeads(@Param("id") UUID id);

    Optional<Company> findByName(String name);

    Optional<Company> findById(UUID id);
}
