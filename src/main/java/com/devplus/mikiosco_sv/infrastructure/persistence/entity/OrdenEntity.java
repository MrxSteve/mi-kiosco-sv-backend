package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import com.devplus.mikiosco_sv.domain.model.OrderStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.OrderStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

// Módulo 5: mapeo parcial para historial de cliente.
// Módulo 6 expandirá esta entidad con los campos restantes.
@Entity
@Table(name = "orden", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "order_number", insertable = false, updatable = false)
    private Long orderNumber;

    @Convert(converter = OrderStatusConverter.class)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
