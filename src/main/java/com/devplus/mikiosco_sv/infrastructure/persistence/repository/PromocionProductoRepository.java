package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.PromocionProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromocionProductoRepository extends JpaRepository<PromocionProductoEntity, PromocionProductoId> {

    List<PromocionProductoEntity> findByIdPromocionId(UUID promocionId);

    void deleteByIdPromocionId(UUID promocionId);

    boolean existsByIdPromocionIdAndIdProductoIdIn(UUID promocionId, List<UUID> productoIds);
}
