package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ConfiguracionComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateConfiguracionRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ConfiguracionComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ConfiguracionComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateConfiguracionUseCase {

    private final ConfiguracionComedorRepository configuracionRepository;
    private final ConfiguracionComedorMapper configuracionMapper;

    @Transactional
    public ConfiguracionComedorResponse execute(UpdateConfiguracionRequest request,
                                                AuthenticatedUser caller) {
        var config = configuracionRepository.findByComedor_Id(caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Configuración del comedor", caller.comedorId()));

        if (request.getCurrencySymbol() != null)    config.setCurrencySymbol(request.getCurrencySymbol());
        if (request.getCurrencyCode() != null)      config.setCurrencyCode(request.getCurrencyCode());
        if (request.getDateFormat() != null)        config.setDateFormat(request.getDateFormat());
        if (request.getTimeFormat() != null)        config.setTimeFormat(request.getTimeFormat());
        if (request.getOpeningTime() != null)       config.setOpeningTime(request.getOpeningTime());
        if (request.getClosingTime() != null)       config.setClosingTime(request.getClosingTime());
        if (request.getMaxLoginAttempts() != null)  config.setMaxLoginAttempts(request.getMaxLoginAttempts());
        if (request.getLockoutMinutes() != null)    config.setLockoutMinutes(request.getLockoutMinutes());
        if (request.getTicketFooter() != null)      config.setTicketFooter(request.getTicketFooter());

        return configuracionMapper.toResponse(configuracionRepository.save(config));
    }
}
