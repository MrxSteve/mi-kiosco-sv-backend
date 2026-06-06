package com.devplus.mikiosco_sv.infrastructure.persistence.repository;

import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {

    // Login de usuario normal (scoped al comedor)
    Optional<UsuarioEntity> findByEmailAndComedor_Id(String email, UUID comedorId);

    // Login de super_admin (no tiene comedor)
    Optional<UsuarioEntity> findByEmailAndRole(String email, UserRole role);

    boolean existsByEmailAndComedor_Id(String email, UUID comedorId);

    boolean existsByRole(UserRole role);

    List<UsuarioEntity> findAllByComedor_Id(UUID comedorId);
}
