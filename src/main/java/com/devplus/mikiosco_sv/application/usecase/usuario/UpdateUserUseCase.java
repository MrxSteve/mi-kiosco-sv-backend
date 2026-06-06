package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.domain.exception.ForbiddenException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateUserRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import com.devplus.mikiosco_sv.presentation.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(UUID userId, UpdateUserRequest request, AuthenticatedUser caller) {
        var usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("Usuario", userId));

        validateOwnership(caller, usuario);

        if (request.getFullName() != null) {
            usuario.setFullName(request.getFullName());
        }

        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            UUID comedorId = usuario.getComedor().getId();
            if (usuarioRepository.existsByEmailAndComedor_Id(request.getEmail(), comedorId)) {
                throw new ConflictException("El email ya está registrado en este comedor");
            }
            usuario.setEmail(request.getEmail().toLowerCase());
        }

        if (request.getRole() != null) {
            if (request.getRole() == UserRole.SUPER_ADMIN) {
                throw new ForbiddenException("No se puede asignar el rol super_admin");
            }
            if (usuario.getId().equals(caller.userId())) {
                throw new ForbiddenException("No puedes cambiar tu propio rol");
            }
            usuario.setRole(request.getRole());
        }

        return userMapper.toResponse(usuarioRepository.save(usuario));
    }

    private void validateOwnership(AuthenticatedUser caller,
                                   com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity usuario) {
        if (caller.isSuperAdmin()) return;
        if (usuario.getComedor() == null
                || !usuario.getComedor().getId().equals(caller.comedorId())) {
            throw new ForbiddenException("No tienes acceso a este usuario");
        }
    }
}
