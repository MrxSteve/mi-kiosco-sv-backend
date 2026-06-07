package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ClienteEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListClientsUseCase {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClientResponse> execute(String q, AuthenticatedUser caller) {
        List<ClienteEntity> clientes = (q != null && !q.isBlank())
                ? clienteRepository.search(caller.comedorId(), q.trim())
                : clienteRepository.findByComedorIdOrderByFullNameAsc(caller.comedorId());

        return clientes.stream().map(this::toResponse).toList();
    }

    ClientResponse toResponse(ClienteEntity e) {
        return ClientResponse.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .address(e.getAddress())
                .customerCode(e.getCustomerCode())
                .notes(e.getNotes())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
