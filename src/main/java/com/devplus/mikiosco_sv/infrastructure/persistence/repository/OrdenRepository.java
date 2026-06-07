package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdenRepository extends JpaRepository<OrdenEntity, UUID> {

    // Historial de cliente (módulo 5)
    List<OrdenEntity> findByClienteIdAndComedorIdOrderByCreatedAtDesc(UUID clienteId, UUID comedorId);

    // Listado del día con rango de fechas
    @Query("""
            SELECT o FROM OrdenEntity o
            WHERE o.comedorId = :comedorId
              AND o.createdAt >= :from
              AND o.createdAt < :to
            ORDER BY o.createdAt DESC
            """)
    List<OrdenEntity> findByComedorIdAndDateRange(
            @Param("comedorId") UUID comedorId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to);

    Optional<OrdenEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    Optional<OrdenEntity> findByComedorIdAndOrderNumber(UUID comedorId, Long orderNumber);

    // Cocina: órdenes enviadas con status activo de cocina, ordenadas por tiempo de llegada
    List<OrdenEntity> findByComedorIdAndSentToKitchenAtIsNotNullAndStatusInOrderBySentToKitchenAtAsc(
            UUID comedorId, List<OrderStatus> statuses);
}
