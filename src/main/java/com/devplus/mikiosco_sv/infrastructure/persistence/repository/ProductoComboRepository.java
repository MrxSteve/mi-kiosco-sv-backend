package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoComboEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoComboRepository extends JpaRepository<ProductoComboEntity, UUID> {

    List<ProductoComboEntity> findByProductoIdAndComedorId(UUID productoId, UUID comedorId);

    List<ProductoComboEntity> findByProductoIdInAndComedorId(List<UUID> productoIds, UUID comedorId);

    Optional<ProductoComboEntity> findByIdAndProductoIdAndComedorId(UUID id, UUID productoId, UUID comedorId);

    boolean existsByProductoIdAndComedorIdAndStatus(UUID productoId, UUID comedorId, GenericStatus status);
}
