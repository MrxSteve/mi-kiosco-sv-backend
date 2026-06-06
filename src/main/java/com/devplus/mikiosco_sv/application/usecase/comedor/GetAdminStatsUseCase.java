package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.UsuarioRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.AdminStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminStatsUseCase {

    private final ComedorRepository comedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse execute() {
        long total    = comedorRepository.count();
        long active   = comedorRepository.countByStatus(GenericStatus.ACTIVE);
        long inactive = comedorRepository.countByStatus(GenericStatus.INACTIVE);
        long users    = usuarioRepository.countByComedorIsNotNull();

        return AdminStatsResponse.builder()
                .totalComedores(total)
                .activeComedores(active)
                .inactiveComedores(inactive)
                .totalUsers(users)
                .build();
    }
}
