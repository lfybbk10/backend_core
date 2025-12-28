package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class LeadEqualsHashCodeTest {

    @Test
    void shouldBeReflexive_whenEqualsCalledOnSameObject() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Объект равен сам себе (isEqualTo использует equals() внутри)
        assertThat(lead).isEqualTo(lead);
    }

    @Test
    void shouldBeSymmetric_whenEqualsCalledOnTwoObjects() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Симметричность — порядок сравнения не важен
        assertThat(lead1).isEqualTo(lead2);
        assertThat(lead2).isEqualTo(lead1);
    }

    @Test
    void shouldBeTransitive_whenEqualsChainOfThreeObjects() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead3 = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Транзитивность — если A=B и B=C, то A=C
        assertThat(lead1).isEqualTo(lead2);
        assertThat(lead2).isEqualTo(lead3);
        assertThat(lead1).isEqualTo(lead3);
    }

    @Test
    void shouldBeConsistent_whenEqualsCalledMultipleTimes() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Результат одинаковый при многократных вызовах
        assertThat(lead1).isEqualTo(lead2);
        assertThat(lead1).isEqualTo(lead2);
        assertThat(lead1).isEqualTo(lead2);
    }

    @Test
    void shouldReturnFalse_whenEqualsComparedWithNull() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Объект не равен null (isNotEqualTo проверяет equals(null) = false)
        assertThat(lead).isNotEqualTo(null);
    }

    @Test
    void shouldHaveSameHashCode_whenObjectsAreEqual() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact, "TestCorp", "NEW");

        // Then: Если объекты равны, то hashCode должен быть одинаковым
        assertThat(lead1).isEqualTo(lead2);
        assertThat(lead1.hashCode()).isEqualTo(lead2.hashCode());
    }

    @Test
    void shouldWorkInHashMap_whenLeadUsedAsKey() {
        // Given
        UUID randomUUID = UUID.randomUUID();
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(randomUUID, contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(randomUUID, contact, "TestCorp", "NEW");

        Map<Lead, String> map = new HashMap<>();
        map.put(lead1, "CONTACTED");

        // When: Получаем значение по другому объекту с тем же id
        String status = map.get(lead2);

        // Then: HashMap нашел значение благодаря equals/hashCode
        assertThat(status).isEqualTo("CONTACTED");
    }

    @Test
    void shouldNotBeEqual_whenIdsAreDifferent() {
        // Given
        Address address = new Address("New York", "Times Square", "123");
        Contact contact = new Contact("John@gmail.com", "+79122543221", address);
        Lead lead1 = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
        Lead lead2 = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

        // Then: Разные id = разные объекты (isNotEqualTo использует equals() внутри)
        assertThat(lead1).isNotEqualTo(lead2);
    }
}