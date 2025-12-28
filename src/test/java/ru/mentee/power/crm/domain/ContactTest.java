package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John", "Doe", address);
        assertThat(contact.email()).isEqualTo("John");
        assertThat(contact.phone()).isEqualTo("Doe");

        assertThat(contact.address().city()).isEqualTo(address.city());
        assertThat(contact.address().street()).isEqualTo(address.street());
        assertThat(contact.address().zip()).isEqualTo(address.zip());
    }

    @Test
    void shouldBeEqual_whenSameData() {
        Address address = new Address("New York", "Times Square", "123");
        Contact contact1 = new Contact("John", "Doe", address);
        Contact contact2 = new Contact("John", "Doe", address);

        assertThat(contact1.equals(contact2)).isTrue();
        assertThat(contact1.hashCode() == contact2.hashCode()).isTrue();
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        Address address = new Address("New York", "Times Square", "123");
        Contact contact1 = new Contact("John", "Doe", address);
        Contact contact2 = new Contact("Michael", "Son", address);

        assertThat(contact1.equals(contact2)).isFalse();
    }

    @Test
    void shouldThrowException_whenAddressIsNull() {
        assertThatNullPointerException().isThrownBy(() -> new Contact("sdasd", "asda", null));
    }
}