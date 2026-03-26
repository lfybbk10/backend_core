package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateLeadRequest {

  @Email(message = "Некорректный формат email")
  private String email;

  private String status;

  public UpdateLeadRequest(String email, String status) {
    this.email = email;
    this.status = status;
  }
}
