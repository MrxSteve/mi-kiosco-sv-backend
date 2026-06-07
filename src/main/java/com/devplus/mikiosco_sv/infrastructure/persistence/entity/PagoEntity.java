package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import com.devplus.mikiosco_sv.domain.model.PaymentMethod;
import com.devplus.mikiosco_sv.domain.model.PaymentStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.PaymentMethodConverter;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter.PaymentStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pago", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "comedor_id", nullable = false)
    private UUID comedorId;

    @Column(name = "orden_id", nullable = false, unique = true)
    private UUID ordenId;

    @Column(name = "amount_received", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountReceived;

    @Column(name = "change_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal changeAmount;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(nullable = false)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.CASH;

    @Convert(converter = PaymentStatusConverter.class)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PAID;

    @Column(name = "transaction_reference", length = 120)
    private String transactionReference;

    @Column(name = "paid_at", nullable = false)
    private OffsetDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
