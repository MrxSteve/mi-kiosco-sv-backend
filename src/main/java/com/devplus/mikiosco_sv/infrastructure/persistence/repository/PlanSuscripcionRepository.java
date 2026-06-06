package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PlanSuscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcionEntity, UUID> {

    List<PlanSuscripcionEntity> findAllByStatus(GenericStatus status);

    Optional<PlanSuscripcionEntity> findByCodeAndStatus(String code, GenericStatus status);
}
