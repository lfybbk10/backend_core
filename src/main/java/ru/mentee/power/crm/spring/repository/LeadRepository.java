package ru.mentee.power.crm.spring.repository;

import ru.mentee.power.crm.domain.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead entity);

    void delete(UUID id);

    Optional<Lead> findById(UUID id);

    Optional<Lead> findByEmail(String email);

    List<Lead> findAll();
}
