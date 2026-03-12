package ru.mentee.power.crm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String industry;

    @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
    private List<Lead> leads = new ArrayList<>();

    public Company(String name, String industry) {
        this.name = name;
        this.industry = industry;
    }

    public Company(String name) {
        this.name = name;
    }

    public void addLead(Lead lead) {
        leads.add(lead);
        lead.setCompany(this);
    }

    public void removeLead(Lead lead) {
        leads.remove(lead);
        lead.setCompany(null);
    }
}
