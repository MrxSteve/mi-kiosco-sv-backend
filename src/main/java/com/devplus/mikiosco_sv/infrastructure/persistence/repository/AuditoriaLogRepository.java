package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.AuditAction;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.AuditoriaLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLogEntity, UUID> {

    Page<AuditoriaLogEntity> findByComedorIdOrderByCreatedAtDesc(UUID comedorId, Pageable pageable);

    Page<AuditoriaLogEntity> findByComedorIdAndActionOrderByCreatedAtDesc(
            UUID comedorId, AuditAction action, Pageable pageable);
}
