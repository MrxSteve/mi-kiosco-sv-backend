package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenDetalleExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrdenDetalleExtraRepository extends JpaRepository<OrdenDetalleExtraEntity, UUID> {

    List<OrdenDetalleExtraEntity> findByOrdenDetalleId(UUID ordenDetalleId);

    List<OrdenDetalleExtraEntity> findByOrdenDetalleIdIn(List<UUID> ordenDetalleIds);
}
