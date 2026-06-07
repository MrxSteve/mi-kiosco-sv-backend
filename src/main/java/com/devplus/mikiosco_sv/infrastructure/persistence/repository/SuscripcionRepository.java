package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.SuscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SuscripcionRepository extends JpaRepository<SuscripcionEntity, UUID> {

    Optional<SuscripcionEntity> findByComedor_IdAndStatus(UUID comedorId, SubscriptionStatus status);

    boolean existsByComedor_IdAndStatus(UUID comedorId, SubscriptionStatus status);

    List<SuscripcionEntity> findAllByOrderByCreatedAtDesc();

    List<SuscripcionEntity> findAllByStatusOrderByCreatedAtDesc(SubscriptionStatus status);

    long countByStatus(SubscriptionStatus status);

    @Query("SELECT s FROM SuscripcionEntity s WHERE s.plan.id = :planId")
    List<SuscripcionEntity> findByPlanId(@Param("planId") UUID planId);

    @Query("SELECT COUNT(s) FROM SuscripcionEntity s WHERE s.plan.id = :planId AND s.status = :status")
    long countByPlanIdAndStatus(@Param("planId") UUID planId, @Param("status") SubscriptionStatus status);
}
