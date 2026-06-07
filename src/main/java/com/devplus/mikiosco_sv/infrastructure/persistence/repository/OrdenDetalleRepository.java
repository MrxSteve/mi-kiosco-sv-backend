package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalleEntity, UUID> {

    List<OrdenDetalleEntity> findByOrdenIdOrderByCreatedAtAsc(UUID ordenId);

    List<OrdenDetalleEntity> findByOrdenIdInOrderByCreatedAtAsc(List<UUID> ordenIds);

    Optional<OrdenDetalleEntity> findByIdAndOrdenIdAndComedorId(UUID id, UUID ordenId, UUID comedorId);
}
