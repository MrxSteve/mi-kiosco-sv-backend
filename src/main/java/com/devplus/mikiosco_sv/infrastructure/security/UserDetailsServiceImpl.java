package com.devplus.mikiosco_sv.infrastructure.security;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository
                .findByEmailAndRole(email, UserRole.SUPER_ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        boolean locked = usuario.getLockedUntil() != null
                && usuario.getLockedUntil().isAfter(OffsetDateTime.now());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRole().name())
                .disabled(usuario.getStatus() == GenericStatus.INACTIVE)
                .accountLocked(locked)
                .build();
    }
}
