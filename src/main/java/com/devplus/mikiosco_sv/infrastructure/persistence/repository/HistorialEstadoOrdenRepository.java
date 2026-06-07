package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.HistorialEstadoOrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistorialEstadoOrdenRepository extends JpaRepository<HistorialEstadoOrdenEntity, UUID> {
}
