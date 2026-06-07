package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.GenericStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "producto_combo", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoComboEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "combo_qty", nullable = false)
    private int comboQty;

    @Column(name = "combo_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal comboPrice;

    @Convert(converter = GenericStatusConverter.class)
    @Column(nullable = false)
    @Builder.Default
    private GenericStatus status = GenericStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
