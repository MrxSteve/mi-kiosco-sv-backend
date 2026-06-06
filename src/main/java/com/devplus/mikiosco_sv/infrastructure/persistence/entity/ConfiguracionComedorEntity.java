package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "configuracion_comedor", schema = "kiosk")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionComedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comedor_id", nullable = false, unique = true)
    private ComedorEntity comedor;

    @Column(name = "currency_symbol", nullable = false, length = 10)
    @Builder.Default
    private String currencySymbol = "$";

    @Column(name = "currency_code", nullable = false, length = 5)
    @Builder.Default
    private String currencyCode = "USD";

    @Column(name = "date_format", nullable = false, length = 30)
    @Builder.Default
    private String dateFormat = "DD/MM/YYYY";

    @Column(name = "time_format", nullable = false, length = 10)
    @Builder.Default
    private String timeFormat = "HH:mm";

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "max_login_attempts", nullable = false)
    @Builder.Default
    private int maxLoginAttempts = 5;

    @Column(name = "lockout_minutes", nullable = false)
    @Builder.Default
    private int lockoutMinutes = 15;

    @Column(name = "ticket_footer", columnDefinition = "TEXT")
    private String ticketFooter;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
