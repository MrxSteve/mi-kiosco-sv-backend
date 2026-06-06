package com.devplus.mikiosco_sv.infrastructure.config;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.domain.model.UserRole;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.UsuarioEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.sysadmin.email}")
    private String sysadminEmail;

    @Value("${app.sysadmin.password}")
    private String sysadminPassword;

    @Value("${app.sysadmin.name:System Administrator}")
    private String sysadminName;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRole(UserRole.SUPER_ADMIN)) {
            log.info("Super admin ya existe — omitiendo inicialización.");
            return;
        }

        var sysadmin = UsuarioEntity.builder()
                .fullName(sysadminName)
                .email(sysadminEmail)
                .passwordHash(passwordEncoder.encode(sysadminPassword))
                .role(UserRole.SUPER_ADMIN)
                .status(GenericStatus.ACTIVE)
                .build();

        usuarioRepository.save(sysadmin);

        log.warn("╔══════════════════════════════════════════════════════╗");
        log.warn("║  SUPER ADMIN CREADO                                  ║");
        log.warn("║  Email   : {}", sysadminEmail);
        log.warn("║  Cambia SYSADMIN_PASSWORD en producción              ║");
        log.warn("╚══════════════════════════════════════════════════════╝");
    }
}
