package ru.mentee.power.crm.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealService {
    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;
    private final LeadService leadService;

    @Transactional
    public Deal convertLeadToDeal(UUID leadId, BigDecimal amount) {
        if(leadRepository.findById(leadId).isPresent()) {
            Deal deal = new Deal(leadId, amount);
            dealRepository.save(deal);

            Lead lead = leadRepository.findById(leadId).orElseThrow();
            lead.setStatus("CONVERTED");
            leadRepository.save(lead);

            return deal;
        }
        else{
            throw new IllegalArgumentException("Lead not found " + leadId);
        }
    }

    public Deal transitionDealStatus(UUID dealId, DealStatus newStatus) {
        Deal deal = dealRepository.findById(dealId).orElse(null);
        if(deal != null) {
            deal.transitionTo(newStatus);
            dealRepository.save(deal);
            return deal;
        }
        else{
            throw new IllegalArgumentException("Deal not found " + dealId);
        }
    }

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
        return getAllDeals().stream().collect(Collectors.groupingBy(Deal::getStatus));
    }
}