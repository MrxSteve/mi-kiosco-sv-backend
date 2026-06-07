package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoExtraEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoExtraId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductoExtraRepository extends JpaRepository<ProductoExtraEntity, ProductoExtraId> {

    List<ProductoExtraEntity> findByIdProductoId(UUID productoId);

    List<ProductoExtraEntity> findByIdProductoIdIn(List<UUID> productoIds);

    void deleteByIdProductoIdAndIdExtraId(UUID productoId, UUID extraId);

    boolean existsByIdProductoIdAndIdExtraId(UUID productoId, UUID extraId);

    int countByIdProductoId(UUID productoId);
}
