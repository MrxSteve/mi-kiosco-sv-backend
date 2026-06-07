package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orden_detalle_extra", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDetalleExtraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "orden_detalle_id", nullable = false)
    private UUID ordenDetalleId;

    @Column(name = "extra_id")
    private UUID extraId;

    @Column(name = "extra_name_snapshot", nullable = false, length = 120)
    private String extraNameSnapshot;

    @Column(name = "extra_price_snapshot", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal extraPriceSnapshot = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
