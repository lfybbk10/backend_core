package ru.mentee.power.crm.spring.rest;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadRestController {
  private final LeadService leadService;

  @GetMapping
  public List<Lead> getAllLeads() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public Lead getLeadById(@PathVariable UUID id) {
    return leadService.findById(id).orElse(null);
  }

  @PostMapping
  public Lead createLead(@RequestBody Lead lead) {
    return leadService.createLead(lead);
  }
}
