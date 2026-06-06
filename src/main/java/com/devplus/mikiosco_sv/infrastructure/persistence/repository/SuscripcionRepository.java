package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.SuscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SuscripcionRepository extends JpaRepository<SuscripcionEntity, UUID> {

    Optional<SuscripcionEntity> findByComedor_IdAndStatus(UUID comedorId, SubscriptionStatus status);

    boolean existsByComedor_IdAndStatus(UUID comedorId, SubscriptionStatus status);
}
