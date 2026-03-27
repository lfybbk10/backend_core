package ru.mentee.power.crm.spring.rest.fixed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateInviteeRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Некорректный формат email")
  private String email;

  @NotBlank(message = "Имя обязательно")
  private String firstName;

  public CreateInviteeRequest(String email, String firstName) {
    this.email = email;
    this.firstName = firstName;
  }
}
