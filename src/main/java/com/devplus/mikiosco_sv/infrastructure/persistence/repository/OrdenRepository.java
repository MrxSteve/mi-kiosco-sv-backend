package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrdenRepository extends JpaRepository<OrdenEntity, UUID> {

    List<OrdenEntity> findByClienteIdAndComedorIdOrderByCreatedAtDesc(UUID clienteId, UUID comedorId);
}
