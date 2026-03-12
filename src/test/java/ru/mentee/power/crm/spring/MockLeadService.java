package ru.mentee.power.crm.spring;

import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.LeadService;
import java.util.*;

public class MockLeadService extends LeadService {
    private final List<Lead> mockLeads;

    public MockLeadService() {
        super(null, null); // repository не используется в mock
        this.mockLeads = List.of(
                new Lead(UUID.randomUUID(), "test1@example.com", new Company("+1234567890"), "NEW"),
                new Lead(UUID.randomUUID(), "test2@example.com", new Company("+0987654321"), "NEW")
        );
    }

    @Override
    public List<Lead> findAll() {
        return mockLeads;
    }
}