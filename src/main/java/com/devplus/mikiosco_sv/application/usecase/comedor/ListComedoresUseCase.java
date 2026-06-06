package com.devplus.mikiosco_sv.application.usecase.comedor;

import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ComedorRepository;
import com.devplus.mikiosco_sv.presentation.dto.response.ComedorResponse;
import com.devplus.mikiosco_sv.presentation.mapper.ComedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListComedoresUseCase {

    private final ComedorRepository comedorRepository;
    private final ComedorMapper comedorMapper;

    @Transactional(readOnly = true)
    public List<ComedorResponse> execute() {
        return comedorMapper.toResponseList(comedorRepository.findAll());
    }
}
