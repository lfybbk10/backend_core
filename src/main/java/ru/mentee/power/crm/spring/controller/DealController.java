package ru.mentee.power.crm.spring.controller;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.DealService;
import ru.mentee.power.crm.spring.service.LeadService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/deals")
public class DealController {
  private final DealService dealService;
  private final LeadService leadService;

  @GetMapping
  public String listDeals(Model model) {
    model.addAttribute("deals", dealService.getAllDeals());
    return "deals/list";
  }

  @GetMapping("/kanban")
  public String kanbanView(Model model) {
    model.addAttribute("dealsByStatus", dealService.getDealsByStatusForKanban());
    return "deals/kanban";
  }

  @GetMapping("/convert/{leadId}")
  public String showConvertForm(@PathVariable UUID leadId, Model model) {
    Lead lead =
        leadService
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
    model.addAttribute("lead", lead);
    return "deals/convert";
  }

  @PostMapping("/convert")
  public String convertLeadToDeal(@RequestParam UUID leadId, @RequestParam BigDecimal amount) {
    dealService.convertLeadToDeal(leadId, amount);
    return "redirect:/deals";
  }

  @PostMapping("/{id}/transition")
  public String transitionStatus(@PathVariable UUID id, @RequestParam DealStatus newStatus) {
    dealService.transitionDealStatus(id, newStatus);
    return "redirect:/deals/kanban";
  }
}
