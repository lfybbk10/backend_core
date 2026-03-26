package ru.mentee.power.crm.spring.dto;

import java.time.Instant;
import java.util.UUID;

public record LeadResponse(
    UUID id, String email, String status, String company, Instant createdAt) {}
