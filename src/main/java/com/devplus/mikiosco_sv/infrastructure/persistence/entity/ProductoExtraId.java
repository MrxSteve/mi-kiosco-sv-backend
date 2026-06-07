package com.devplus.mikiosco_sv.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductoExtraId implements Serializable {

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(name = "extra_id")
    private UUID extraId;
}
