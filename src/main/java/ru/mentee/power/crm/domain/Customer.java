package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(UUID id, Contact contact, Address billingAddress, String loyaltyTier) {
    public Customer {
        if(id == null || contact == null || billingAddress == null || loyaltyTier == null) {
            throw new NullPointerException();
        }
        if(!loyaltyTier.equals("BRONZE") && !loyaltyTier.equals("SILVER") && !loyaltyTier.equals("GOLD")) {
            throw new IllegalArgumentException();
        }
    }
}
