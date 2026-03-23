package ru.mentee.power.crm.spring.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealJpaRepository extends JpaRepository<Deal, UUID> {

    @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})
    @Query("select d from Deal d where d.id = :id")
    Optional<Deal> findDealWithProducts(UUID id);
}
