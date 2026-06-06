package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.UpdateComedorRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateComedorUseCase {

    private final ComedorRepository comedorRepository;
    private final ComedorMapper comedorMapper;

    @Transactional
    public ComedorResponse execute(UpdateComedorRequest request, AuthenticatedUser caller) {
        var comedor = comedorRepository.findById(caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Comedor", caller.comedorId()));

        if (request.getName() != null)      comedor.setName(request.getName());
        if (request.getLegalName() != null)  comedor.setLegalName(request.getLegalName());
        if (request.getPhone() != null)      comedor.setPhone(request.getPhone());
        if (request.getAddress() != null)    comedor.setAddress(request.getAddress());
        if (request.getLogoUrl() != null)    comedor.setLogoUrl(request.getLogoUrl());
        if (request.getTimezone() != null)   comedor.setTimezone(request.getTimezone());

        return comedorMapper.toResponse(comedorRepository.save(comedor));
    }
}
