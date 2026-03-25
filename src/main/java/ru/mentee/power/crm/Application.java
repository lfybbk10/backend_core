package ru.mentee.power.crm;

import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Product;
import ru.mentee.power.crm.entity.DealProduct;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.repository.DealJpaRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.ProductJpaRepository;
import ru.mentee.power.crm.spring.service.LeadService;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner seedLeads(CompanyRepository companyRepository, ProductJpaRepository productRepository, LeadService leadService) {
        return args -> {
//            Company company1 = companyRepository.findByName("Сбер").get();
//            Company company2 = companyRepository.findByName("Яндекс").get();
//
//
//            leadService.addLead("test2@mail.com", company1, "CONVERTED");
//            leadService.addLead("test3@mail.com", company2, "CONVERTED");
//            leadService.addLead("test4@mail.com", company1, "NEW");
//            leadService.addLead("test5@mail.com", company2, "QUALIFIED");

//            Deal deal = new Deal();
//            deal.setAmount(new BigDecimal(150000));
//
//            Product product1 = new Product();
//            product1.setName("Ноутбук Dell");
//            product1.setPrice(new BigDecimal(150000));
//            product1.setSku("LAPTOP-001");
//
//            Product product2 = new Product();
//            product2.setName("Монитор LG");
//            product2.setPrice(new BigDecimal(25000));
//            product2.setSku("MONITOR-001");
//
//            Product product3 = new Product();
//            product3.setName("Playstation 4");
//            product3.setPrice(new BigDecimal(50000));
//            product3.setSku("PS-001");
//
//            productRepository.save(product1);
//            productRepository.save(product2);
//            productRepository.save(product3);
//
//            DealProduct dealProduct1 = new DealProduct();
//            dealProduct1.setProduct(product1);
//            dealProduct1.setQuantity(2);
//            dealProduct1.setUnitPrice(new BigDecimal(81000));
//
//            DealProduct dealProduct2 = new DealProduct();
//            dealProduct2.setProduct(product2);
//            dealProduct2.setQuantity(1);
//            dealProduct2.setUnitPrice(new BigDecimal(20000));
//
//            DealProduct dealProduct3 = new DealProduct();
//            dealProduct3.setProduct(product3);
//            dealProduct3.setQuantity(1);
//            dealProduct3.setUnitPrice(new BigDecimal(50000));
//
//            deal.addDealProduct(dealProduct1);
//            deal.addDealProduct(dealProduct2);
//            deal.addDealProduct(dealProduct3);
//
//            UUID dealId = dealRepository.save(deal).getId();
        };
    }
}
