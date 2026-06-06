package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetComedorUseCase {

    private final ComedorRepository comedorRepository;
    private final ComedorMapper comedorMapper;

    @Transactional(readOnly = true)
    public ComedorResponse execute(AuthenticatedUser caller) {
        var comedor = comedorRepository.findById(caller.comedorId())
                .orElseThrow(() -> NotFoundException.of("Comedor", caller.comedorId()));
        return comedorMapper.toResponse(comedor);
    }
}
