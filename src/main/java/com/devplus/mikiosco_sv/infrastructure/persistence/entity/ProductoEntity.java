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
@Table(name = "producto", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "categoria_id", nullable = false)
    private UUID categoriaId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "has_extras", nullable = false)
    @Builder.Default
    private boolean hasExtras = false;

    @Column(name = "has_combos", nullable = false)
    @Builder.Default
    private boolean hasCombos = false;

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
