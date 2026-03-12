package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.domain.Company;
import ru.mentee.power.crm.spring.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.spring.repository.LeadRepository;
import ru.mentee.power.crm.spring.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
//        LeadService leadService = new LeadService(null, null);
//        leadService.addLead("test1@mail.com", new Company("comp1"), "NEW");
//        leadService.addLead("test2@mail.com", "comp2", "NEW");
//        leadService.addLead("test3@mail.com", "comp3", "NEW");
//        leadService.addLead("test4@mail.com", "comp2", "NEW");
//        leadService.addLead("test5@mail.com", "comp1", "NEW");
//
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(8080);
//        tomcat.getConnector();
//
//        Context context = tomcat.addContext("", new File(".").getAbsolutePath());
//        context.getServletContext().setAttribute("leadService", leadService);
//        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
//        context.addServletMappingDecoded("/leads", "LeadListServlet");
//        tomcat.start();
//        System.out.println("Tomcat started on port 8080");
//        System.out.println("Open http://localhost:8080/leads in browser");
//
//        tomcat.getServer().await();
    }
}
