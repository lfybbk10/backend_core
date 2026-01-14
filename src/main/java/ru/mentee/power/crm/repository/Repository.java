package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    void add(T entity);

    void remove(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();
}
