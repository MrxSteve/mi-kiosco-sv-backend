package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.domain.exception.NotFoundException;
import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToggleComedorStatusUseCase {

    private final ComedorRepository comedorRepository;
    private final ComedorMapper comedorMapper;

    @Transactional
    public ComedorResponse execute(UUID comedorId) {
        var comedor = comedorRepository.findById(comedorId)
                .orElseThrow(() -> NotFoundException.of("Comedor", comedorId));

        comedor.setStatus(comedor.getStatus() == GenericStatus.ACTIVE
                ? GenericStatus.INACTIVE
                : GenericStatus.ACTIVE);

        return comedorMapper.toResponse(comedorRepository.save(comedor));
    }
}
