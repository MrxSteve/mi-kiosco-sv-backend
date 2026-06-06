package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.domain.exception.ForbiddenException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.UserResponse;
import com.devplus.mikiosco_sv.presentation.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleUserStatusUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(UUID userId, AuthenticatedUser caller) {
        var usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("Usuario", userId));

        validateOwnership(caller, usuario);

        if (usuario.getId().equals(caller.userId())) {
            throw new ForbiddenException("No puedes desactivar tu propia cuenta");
        }

        GenericStatus newStatus = usuario.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE;

        usuario.setStatus(newStatus);
        return userMapper.toResponse(usuarioRepository.save(usuario));
    }

    private void validateOwnership(AuthenticatedUser caller, UsuarioEntity usuario) {
        if (caller.isSuperAdmin()) return;
        if (usuario.getComedor() == null
                || !usuario.getComedor().getId().equals(caller.comedorId())) {
            throw new ForbiddenException("No tienes acceso a este usuario");
        }
    }
}
