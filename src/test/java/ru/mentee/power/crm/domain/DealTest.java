package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class DealTest {

    @Test
    void shouldCreateDeal_withNewStatus() {
        BigDecimal amount = new BigDecimal("100000.00");

        Deal deal = new Deal();
        deal.setAmount(amount);

        assertThat(deal.getId()).isNotNull();
        assertThat(deal.getAmount()).isEqualTo(amount);
        assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
        assertThat(deal.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldTransitionToValidStatus() {
        UUID leadId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100000.00");

        Deal deal = new Deal();
        deal.setStatus(DealStatus.NEW);
        deal.transitionTo(DealStatus.QUALIFIED);
        assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
    }
}