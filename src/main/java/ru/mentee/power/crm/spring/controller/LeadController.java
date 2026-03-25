package ru.mentee.power.crm.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LeadController {
    private final LeadService leadService;
    private final CompanyRepository companyRepository;

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
    }

    @GetMapping("/leads")
    public String showLeads(@RequestParam(required=false) String search,
                            @RequestParam(required = false) String status,
                            Model model) {
        List<Lead> leads = leadService.findLeads(search, status);
        model.addAttribute("leads", leads);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("status", status != null ? status : "");
        return "leads/list";
    }

    @GetMapping("/leads/new")
    public String showCreateForm(Model model) {
        model.addAttribute("lead", new Lead(null, "", new Company(), "NEW"));
        model.addAttribute("companies", companyRepository.findAll());
        return "leads/create"; // JTE шаблон leads/create.jte
    }

    @PostMapping("/leads")
    public String createLead(@Valid @ModelAttribute Lead lead, @RequestParam UUID companyId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "leads/create";
        }
        Company company = companyRepository.findById(companyId).get();
        lead.setCompany(company);
        leadService.addLead(lead.getEmail(), company, lead.getStatus());
        return "redirect:/leads";
    }

    @GetMapping("/leads/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Lead lead = leadService.findById(id).orElse(null);
        if(lead != null) {
            model.addAttribute("lead", lead);
            model.addAttribute("companies", companyRepository.findAll());
            return "leads/edit";
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found");
        }
    }

    @PostMapping("/leads/{id}")
    public String updateLead(@Valid @ModelAttribute Lead lead, @RequestParam UUID companyId, BindingResult bindingResult, @PathVariable UUID id) {
        if(bindingResult.hasErrors()) {
            return "leads/edit";
        }
        Optional<Company> company = companyRepository.findById(companyId);
        company.ifPresent(lead::setCompany);
        leadService.update(id, lead);
        return "redirect:/leads";
    }

    @PostMapping("/leads/{id}/delete")
    public String deleteLead(@PathVariable UUID id) {
        leadService.delete(id);
        return "redirect:/leads";
    }


}
