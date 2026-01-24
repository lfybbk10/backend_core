package ru.mentee.power.crm.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.mentee.power.crm.spring.service.LeadService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner seedLeads(LeadService leadService) {
        return args -> {
            leadService.addLead("test1@mail.com", "comp1", "NEW");
            leadService.addLead("test2@mail.com", "comp2", "CONVERTED");
            leadService.addLead("test3@mail.com", "comp3", "CONVERTED");
            leadService.addLead("test4@mail.com", "comp2", "NEW");
            leadService.addLead("test5@mail.com", "comp1", "QUALIFIED");
        };
    }
}
