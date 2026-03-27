package ru.mentee.power.crm.spring.rest.fixed;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitee {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;

  @NotBlank(message = "Имя обязательно")
  private String firstName;

  private Instant createdAt;
}
