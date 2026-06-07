package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.OrderStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estado_orden", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoOrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "orden_id", nullable = false)
    private UUID ordenId;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "previous_status")
    private OrderStatus previousStatus;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "new_status", nullable = false)
    private OrderStatus newStatus;

    @Column(name = "changed_by_user_id")
    private UUID changedByUserId;

    @Column(name = "change_notes")
    private String changeNotes;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;
}
