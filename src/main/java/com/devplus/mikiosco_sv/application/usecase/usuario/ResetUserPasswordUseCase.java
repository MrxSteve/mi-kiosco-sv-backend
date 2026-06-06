package com.devplus.mikiosco_sv.application.usecase.usuario;

import com.devplus.mikiosco_sv.domain.exception.ForbiddenException;
import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetUserPasswordUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(UUID userId, String newPassword, AuthenticatedUser caller) {
        var usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> NotFoundException.of("Usuario", userId));

        // Admin solo puede resetear contraseñas dentro de su comedor
        if (!caller.isSuperAdmin()) {
            if (usuario.getComedor() == null
                    || !usuario.getComedor().getId().equals(caller.comedorId())) {
                throw new ForbiddenException("No tienes acceso a este usuario");
            }
        }

        // No se puede resetear la contraseña de un super_admin desde un rol menor
        if (usuario.getRole() == UserRole.SUPER_ADMIN && !caller.isSuperAdmin()) {
            throw new ForbiddenException("No tienes permiso para cambiar esta contraseña");
        }

        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuario.setFailedLoginCount(0);
        usuario.setLockedUntil(null);
        usuarioRepository.save(usuario);
    }
}
