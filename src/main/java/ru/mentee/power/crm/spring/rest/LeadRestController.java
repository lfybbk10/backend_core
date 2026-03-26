package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class LeadRestController {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    return ResponseEntity.ok(leadService.findAll().stream().map(leadMapper::toResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(@PathVariable UUID id) {
    Optional<Lead> optionalLead = leadService.findById(id);
    return optionalLead
        .map(lead -> ResponseEntity.ok(leadMapper.toResponse(lead)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@RequestBody CreateLeadRequest request) {
    Lead createdLead = leadService.createLead(leadMapper.toEntity(request));
    LeadResponse leadResponse = leadMapper.toResponse(createdLead);
    URI location = URI.create("/api/leads/" + createdLead.getId());
    return ResponseEntity.created(location).body(leadResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable UUID id, @RequestBody UpdateLeadRequest request) {
    Optional<Lead> existingLead = leadService.findById(id);

    if (existingLead.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Lead lead = existingLead.get();
    leadMapper.updateEntity(request, lead);

    return leadService
        .update(id, lead)
        .map(updatedLead -> ResponseEntity.ok(leadMapper.toResponse(updatedLead)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    boolean success = leadService.delete(id);
    if (success) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
