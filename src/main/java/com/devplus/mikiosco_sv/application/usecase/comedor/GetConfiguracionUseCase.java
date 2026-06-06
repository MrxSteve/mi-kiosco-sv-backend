package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ConfiguracionComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ConfiguracionComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ConfiguracionComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetConfiguracionUseCase {

    private final ConfiguracionComedorRepository configuracionRepository;
    private final ConfiguracionComedorMapper configuracionMapper;

    @Transactional(readOnly = true)
    public ConfiguracionComedorResponse execute(AuthenticatedUser caller) {
        var config = configuracionRepository.findByComedor_Id(caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Configuración del comedor", caller.comedorId()));
        return configuracionMapper.toResponse(config);
    }
}
