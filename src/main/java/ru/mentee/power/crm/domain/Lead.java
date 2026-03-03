package ru.mentee.power.crm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Компания обязательна")
    @Column(nullable = false)
    private String company;

    @NotBlank(message = "Статус обязателен")
    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Lead(UUID id, String email, String company, String status) {
        this.id = id;
        this.email = email;
        this.company = company;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // equals/hashCode ТОЛЬКО по id (как у тебя в record)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lead lead)) return false;
        return id != null && id.equals(lead.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}