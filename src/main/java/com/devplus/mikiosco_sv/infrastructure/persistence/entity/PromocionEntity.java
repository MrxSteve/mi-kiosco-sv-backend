package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.DiscountTypeConverter;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.GenericStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "promocion", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(nullable = false, length = 60)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Convert(converter = DiscountTypeConverter.class)
    @Column(name = "discount_kind", nullable = false)
    private DiscountType discountKind;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Column(name = "starts_at")
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses_count", nullable = false)
    @Builder.Default
    private int usesCount = 0;

    @Column(name = "applies_to_all", nullable = false)
    @Builder.Default
    private boolean appliesToAll = true;

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
