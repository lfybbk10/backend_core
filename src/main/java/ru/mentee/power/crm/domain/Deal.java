package ru.mentee.power.crm.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.entity.DealProduct;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deals")
public class Deal {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "lead_id")
  private Lead lead;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "stage")
  @Enumerated(EnumType.STRING)
  private DealStatus status = DealStatus.NEW;

  @Column(name = "title")
  private String title = "";

  @Column(name = "created_at")
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DealProduct> dealProducts = new ArrayList<>();

  public void transitionTo(DealStatus newStatus) {
    if (status.canTransitionTo(newStatus)) {
      status = newStatus;
    } else {
      throw new IllegalArgumentException("Cannot transition from " + status + " to " + newStatus);
    }
  }

  public void addDealProduct(DealProduct dealProduct) {
    dealProducts.add(dealProduct);
    dealProduct.setDeal(this);
  }

  public void removeDealProduct(DealProduct dealProduct) {
    dealProducts.remove(dealProduct);
    dealProduct.setDeal(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Deal deal = (Deal) o;
    return Objects.equals(id, deal.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
