package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(UUID id, Contact contact, String company, String status) {
    public Lead {
        if(id == null || contact == null || status == null) {
            throw new NullPointerException();
        }
        if(!status.equals("NEW") && !status.equals("QUALIFIED") && !status.equals("CONVERTED")) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lead && ((Lead) obj).id.equals(id);
    }
}
