package ru.mentee.power.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.*;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @NotBlank(message = "Статус обязателен")
  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Version
  @Column(name = "version", nullable = false)
  @Setter(AccessLevel.NONE) // JPA управляет версией сам — НЕ создаём setter
  private Long version;

  @OneToMany(
      mappedBy = "lead",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JsonIgnore
  public List<Deal> deals = new ArrayList<>();

  public Lead(UUID id, String email, Company company, String status) {
    this.id = id;
    this.email = email;
    this.company = company;
    this.status = status;
    this.createdAt = Instant.now();
  }

  public Lead(String email, Company company, String status) {
    this.email = email;
    this.company = company;
    this.status = status;
    this.createdAt = Instant.now();
  }

  public Lead(String email, String status) {
    this.email = email;
    this.status = status;
    this.createdAt = Instant.now();
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
