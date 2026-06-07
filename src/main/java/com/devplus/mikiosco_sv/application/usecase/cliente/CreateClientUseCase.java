package com.devplus.mikiosco_sv.application.usecase.cliente;

import com.devplus.mikiosco_sv.domain.exception.ConflictException;
import com.devplus.mikiosco_sv.infrastructure.persistence.entity.ClienteEntity;
import com.devplus.mikiosco_sv.infrastructure.persistence.repository.ClienteRepository;
import com.devplus.mikiosco_sv.infrastructure.security.AuthenticatedUser;
import com.devplus.mikiosco_sv.presentation.dto.request.CreateClientRequest;
import com.devplus.mikiosco_sv.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class CreateClientUseCase {

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ClienteRepository clienteRepository;
    private final ListClientsUseCase listClientsUseCase;

    @Transactional
    public ClientResponse execute(CreateClientRequest request, AuthenticatedUser caller) {
        var comedorId = caller.comedorId();

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (clienteRepository.existsByComedorIdAndEmailIgnoreCase(comedorId, request.getEmail())) {
                throw new ConflictException("Ya existe un cliente registrado con ese email");
            }
        }

        var entity = ClienteEntity.builder()
                .comedorId(comedorId)
                .fullName(request.getFullName())
                .email(request.getEmail() != null ? request.getEmail().toLowerCase() : null)
                .phone(request.getPhone())
                .address(request.getAddress())
                .customerCode(generateUniqueCode(comedorId))
                .notes(request.getNotes())
                .build();

        return listClientsUseCase.toResponse(clienteRepository.save(entity));
    }

    private String generateUniqueCode(java.util.UUID comedorId) {
        String code;
        do {
            code = "CLI-" + randomSuffix();
        } while (clienteRepository.existsByComedorIdAndCustomerCode(comedorId, code));
        return code;
    }

    private String randomSuffix() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
