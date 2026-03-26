package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateLeadRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;

  @NotBlank(message = "Статус обязателен")
  private String status;

  public CreateLeadRequest(String email, String status) {
    this.email = email;
    this.status = status;
  }
}
