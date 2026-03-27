package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.generated.LeadManagementApi;
import ru.mentee.power.crm.spring.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Validated
public class LeadRestController implements LeadManagementApi {

  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @Override
  public ResponseEntity<List<LeadResponse>> getLeads() {
    return ResponseEntity.ok(leadService.findAll().stream().map(leadMapper::toResponse).toList());
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    LeadResponse leadResponse = leadService.findById(id);
    return ResponseEntity.ok(leadResponse);
  }

  @Override
  public ResponseEntity<LeadResponse> createLead(CreateLeadRequest request) {
    Lead createdLead = leadService.createLead(leadMapper.toEntity(request));
    LeadResponse leadResponse = leadMapper.toResponse(createdLead);
    URI location = URI.create("/api/leads/" + createdLead.getId());
    return ResponseEntity.created(location).body(leadResponse);
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, UpdateLeadRequest request) {
    return ResponseEntity.ok(leadService.update(id, request));
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
