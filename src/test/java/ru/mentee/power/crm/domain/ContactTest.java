package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ContactTest {

    @Test
    void shouldCreateContact_whenValidData() {
        Contact contact = new Contact("John", "Doe", "john@example.com");
        assertThat(contact.firstName()).isEqualTo("John");
        assertThat(contact.lastName()).isEqualTo("Doe");
        assertThat(contact.email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        Contact contact1 = new Contact("John", "Doe", "john@example.com");
        Contact contact2 = new Contact("John", "Doe", "john@example.com");

        assertThat(contact1.equals(contact2)).isEqualTo(true);
        assertThat(contact1.hashCode() == contact2.hashCode()).isEqualTo(true);
    }

    @Test
    void shouldNotBeEqual_whenDifferentData() {
        Contact contact1 = new Contact("John", "Doe", "john@example.com");
        Contact contact2 = new Contact("Michael", "Son", "mic@example.com");

        assertThat(contact1.equals(contact2)).isFalse();
    }
}