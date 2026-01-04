package ru.mentee.power.crm.infrastructure;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLeadRepository implements Repository<Lead> {
    private final List<Lead> storage = new ArrayList<>();

    @Override
    public void add(Lead entity) {
        if(!storage.contains(entity) && entity != null) {
            storage.add(entity);
        }
    }

    @Override
    public void remove(Lead entity) {
        storage.remove(entity);
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return storage.stream().filter(lead->lead.id().equals(id)).findFirst();
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(storage);
    }
}
