package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<ProductoEntity, UUID> {

    List<ProductoEntity> findByComedorIdOrderByNameAsc(UUID comedorId);

    List<ProductoEntity> findByComedorIdAndStatusOrderByNameAsc(UUID comedorId, GenericStatus status);

    List<ProductoEntity> findByComedorIdAndCategoriaIdOrderByNameAsc(UUID comedorId, UUID categoriaId);

    List<ProductoEntity> findByComedorIdAndCategoriaIdAndStatusOrderByNameAsc(
            UUID comedorId, UUID categoriaId, GenericStatus status);

    Optional<ProductoEntity> findByIdAndComedorId(UUID id, UUID comedorId);

    boolean existsByComedorIdAndNameIgnoreCase(UUID comedorId, String name);

    boolean existsByComedorIdAndNameIgnoreCaseAndIdNot(UUID comedorId, String name, UUID id);
}
