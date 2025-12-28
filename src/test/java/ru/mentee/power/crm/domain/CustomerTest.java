package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldReuseContact_whenCreatingCustomer() {
        Address contactAddress = new Address("Moscow", "Pushkina", "111");
        Address billingAddress = new Address("St. Petersburg", "Lenina", "222");

        Contact contact = new Contact("test@gmail.com", "+79102345432", contactAddress);
        Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "BRONZE");

        assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
    }

    @Test
    void shouldDemonstrateContactReuse_acrossLeadAndCustomer() {
        Address contactAddress = new Address("Moscow", "Pushkina", "111");
        Address billingAddress = new Address("St. Petersburg", "Lenina", "222");
        Contact contact = new Contact("test@gmail.com", "+79102345432", contactAddress);

        Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "BRONZE");
        Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

        assertThat(customer.contact()).isEqualTo(lead.contact());
    }
}