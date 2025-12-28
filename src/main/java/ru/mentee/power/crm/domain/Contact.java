package ru.mentee.power.crm.domain;

public record Contact(String email, String phone, Address address) {
    public Contact {
        if(email == null || phone == null || address == null) {
            throw new NullPointerException();
        }
    }
}
