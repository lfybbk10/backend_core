package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Validated
public class LeadRestController {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    return ResponseEntity.ok(leadService.findAll().stream().map(leadMapper::toResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id) {
    LeadResponse leadResponse = leadService.findById(id);
    return ResponseEntity.ok(leadResponse);
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody CreateLeadRequest request) {
    Lead createdLead = leadService.createLead(leadMapper.toEntity(request));
    LeadResponse leadResponse = leadMapper.toResponse(createdLead);
    URI location = URI.create("/api/leads/" + createdLead.getId());
    return ResponseEntity.created(location).body(leadResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable UUID id, @RequestBody UpdateLeadRequest request) {
    return ResponseEntity.ok(leadService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    leadService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
