package ru.mentee.power.crm.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LeadController {
    private final LeadService leadService;

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
    }

    @GetMapping("/leads")
    public String showLeads(@RequestParam(required = false) String status, Model model) {
        List<Lead> leads = status == null ? leadService.findAll() : leadService.findByStatus(status);
        model.addAttribute("leads", leads);
        model.addAttribute("currentFilter", status);
        return "leads/list";
    }

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        model.addAttribute("lead", new Lead(null, "", "", "NEW"));
        return "leads/create"; // JTE шаблон leads/create.jte
    }

    @PostMapping("/leads")
    public String createLead(@ModelAttribute Lead lead) {
        leadService.addLead(lead.email(), lead.company(), lead.status());
        return "redirect:/leads"; // заменить на redirect:/leads
    }
}
