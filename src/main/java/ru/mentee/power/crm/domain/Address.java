package ru.mentee.power.crm.domain;

public record Address(String city, String street, String zip) {
    public Address {
        java.util.Objects.requireNonNull(city, zip);
        if (city.isBlank() || zip.isBlank()) {
            throw new IllegalArgumentException("city or zip cannot be empty");
        }
    }
}
