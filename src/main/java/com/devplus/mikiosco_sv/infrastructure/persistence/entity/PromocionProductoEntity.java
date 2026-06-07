package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "promocion_producto", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionProductoEntity {

    @EmbeddedId
    private PromocionProductoId id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
