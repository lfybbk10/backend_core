package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AddressTest {

    @Test
    void shouldCreateAddress_whenValidData() {
        Address address = new Address("San Francisco", "123 Main St", "94105");
        assertThat(address.city()).isEqualTo("San Francisco");
        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.zip()).isEqualTo("94105");
    }

    @Test
    void shouldBeEqual_whenSameData() {
        Address address1 = new Address("San Francisco", "123 Main St", "94105");
        Address address2 = new Address("San Francisco", "123 Main St", "94105");

        assertThat(address1.equals(address2)).isTrue();
        assertThat(address1.hashCode() == address2.hashCode()).isTrue();
    }

    @Test
    void shouldThrowException_whenCityIsNull() {
        assertThatNullPointerException().isThrownBy(() -> new Address(null, "", "23"));
    }

    @Test
    void shouldThrowException_whenZipIsBlank() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Address("Test", "qwerty", ""));
    }
}