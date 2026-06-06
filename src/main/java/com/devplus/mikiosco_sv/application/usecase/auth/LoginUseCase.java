package com.devplus.mikiosco_sv.application.usecase.auth;

import com.devplus.mikiosco_sv.domain.exception.ForbiddenException;
import com.devplus.mikiosco_sv.domain.exception.UnauthorizedException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.infrastructure.security.JwtService;
import com.devplus.mikiosco_sv.presentation.dto.request.LoginRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    // TODO: Defaults hasta que se implemente ConfiguracionComedorEntity en Modulo 2
    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final int DEFAULT_LOCKOUT_MINUTES = 15;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse execute(LoginRequest request) {
        UsuarioEntity usuario = findUsuario(request);

        validateAccountNotLocked(usuario);
        validateAccountActive(usuario);
        validatePassword(request.getPassword(), usuario);
        validateComedorActive(usuario);

        markLoginSuccess(usuario);

        UUID comedorId = usuario.getComedor() != null ? usuario.getComedor().getId() : null;
        String token = jwtService.generateToken(
                usuario.getId(), comedorId, usuario.getRole(), usuario.getEmail()
        );

        return LoginResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .user(LoginResponse.UserInfo.builder()
                        .id(usuario.getId())
                        .fullName(usuario.getFullName())
                        .email(usuario.getEmail())
                        .role(usuario.getRole())
                        .comedorId(comedorId)
                        .build())
                .build();
    }

    private UsuarioEntity findUsuario(LoginRequest request) {
        if (request.getComedorId() == null) {
            // Login de super_admin: busca solo por email con ese rol
            return usuarioRepository
                    .findByEmailAndRole(request.getEmail(), UserRole.SUPER_ADMIN)
                    .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        }
        // Login de usuario normal: scoped al comedor
        return usuarioRepository
                .findByEmailAndComedor_Id(request.getEmail(), request.getComedorId())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
    }

    private void validateAccountNotLocked(UsuarioEntity usuario) {
        if (usuario.getLockedUntil() != null
                && usuario.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new UnauthorizedException(
                    "Cuenta bloqueada temporalmente. Intente de nuevo más tarde."
            );
        }
    }

    private void validateAccountActive(UsuarioEntity usuario) {
        if (usuario.getStatus() == GenericStatus.INACTIVE) {
            throw new UnauthorizedException("Cuenta desactivada. Contacte al administrador.");
        }
    }

    private void validatePassword(String rawPassword, UsuarioEntity usuario) {
        if (!passwordEncoder.matches(rawPassword, usuario.getPasswordHash())) {
            int newCount = usuario.getFailedLoginCount() + 1;
            usuario.setFailedLoginCount(newCount);

            if (newCount >= DEFAULT_MAX_ATTEMPTS) {
                usuario.setLockedUntil(OffsetDateTime.now().plusMinutes(DEFAULT_LOCKOUT_MINUTES));
                usuarioRepository.save(usuario);
                throw new UnauthorizedException(
                        "Demasiados intentos fallidos. Cuenta bloqueada por "
                                + DEFAULT_LOCKOUT_MINUTES + " minutos."
                );
            }

            usuarioRepository.save(usuario);
            throw new UnauthorizedException("Credenciales inválidas");
        }
    }

    private void validateComedorActive(UsuarioEntity usuario) {
        if (usuario.getComedor() != null
                && usuario.getComedor().getStatus() == GenericStatus.INACTIVE) {
            throw new ForbiddenException("El comedor está inactivo. Contacte al administrador.");
        }
    }

    private void markLoginSuccess(UsuarioEntity usuario) {
        usuario.setFailedLoginCount(0);
        usuario.setLockedUntil(null);
        usuario.setLastLoginAt(OffsetDateTime.now());
        usuarioRepository.save(usuario);
    }
}
