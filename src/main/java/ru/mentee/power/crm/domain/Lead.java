package ru.mentee.power.crm.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

public record Lead(UUID id,
                   @NotBlank(message = "Email обязателен") @Email(message = "Некорректный формат email") String email,
                   @NotBlank(message = "Компания обязательна") String company,
                   @NotBlank(message = "Статус обязателен") String status) {
    public Lead {
        if(!status.equals("NEW") && !status.equals("QUALIFIED") && !status.equals("CONVERTED")) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lead && ((Lead) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
